package com.restapi.productservice.service.impl;

import com.restapi.productservice.dto.request.CreateProductReq;
import com.restapi.productservice.dto.request.LockProductItem;
import com.restapi.productservice.dto.request.LockProductReq;
import com.restapi.productservice.dto.request.ProductFilter;
import com.restapi.productservice.dto.response.ProductResponse;
import com.restapi.productservice.dto.response.ProductVariantResponse;
import com.restapi.productservice.entity.Category;
import com.restapi.productservice.entity.Product;
import com.restapi.productservice.entity.ProductVariant;
import com.restapi.productservice.mapper.ProductMapper;
import com.restapi.productservice.repository.CategoryRepository;
import com.restapi.productservice.repository.ProductRepository;
import com.restapi.productservice.repository.ProductVariantRepository;
import com.restapi.productservice.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import com.restapi.productservice.exception.AppException;
import com.restapi.productservice.exception.ErrorCode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ProductVariantRepository productVariantRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;
    RedissonClient redissonClient;

    @Override
    @Transactional
    public Product create(CreateProductReq createProductReq) {
        Category category = null;
        if (createProductReq.getCategoryId() != null) {
            category = categoryRepository.findById(createProductReq.getCategoryId()).orElse(null);
        }
        Product product = Product.builder()
                .id(createProductReq.getId())
                .name(createProductReq.getName())
                .description(createProductReq.getDescription())
                .price(createProductReq.getPrice())
                .stock(createProductReq.getStock() != null ? createProductReq.getStock() : 0)
                .category(category)
                .imageUrl(createProductReq.getImageUrl())
                .brand(createProductReq.getBrand())
                .slug(createProductReq.getName() != null 
                        ? createProductReq.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-" + UUID.randomUUID().toString().substring(0, 8) 
                        : UUID.randomUUID().toString())
                .build();
        return productRepository.save(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"product_search"}, allEntries = true)
    public void lock(LockProductReq lockProductReq) {
        if (lockProductReq == null || lockProductReq.getItems() == null || lockProductReq.getItems().isEmpty()) {
            return;
        }
        List<LockProductItem> items = lockProductReq.getItems();

        // instance 1: => update product: 1,2 => key: product:1,2
        // instance 2: => update product: 2,1 => key: product:1,2

        // 1. Tạo một khóa dựa trên danh sách ID sản phẩm để đảm bảo thứ tự
        // Sắp xếp ID để tránh Deadlock giữa các request
        List<String> sortedIds = items.stream()
                .map(LockProductItem::getId)
                .sorted()
                .collect(Collectors.toList());

        String lockKey = "lock:products:" + String.join(",", sortedIds);
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 2. Thử lấy lock trong 10s, giữ lock trong 5s
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                Thread.sleep(4000);
                log.info("Acquired Redis lock for: {}", lockKey);

                var productIdQuantityMap = items.stream()
                        .collect(Collectors.toMap(LockProductItem::getId, LockProductItem::getQuantity, Integer::sum));

                // 3. Lúc này không dùng findByIdInForUpdate nữa, chỉ dùng findByIdIn thường
                List<Product> products = productRepository.findByIdIn(new ArrayList<>(productIdQuantityMap.keySet()));

                if (products.isEmpty()) {
                    throw new RuntimeException("Product not found");
                }

                // 4. Khấu trừ tồn kho trong memory
                products.forEach(product -> {
                    int remainStock = (product.getStock() != null ? product.getStock() : 0) - productIdQuantityMap.get(product.getId());
                    if (remainStock < 0) {
                        throw new RuntimeException("Product " + product.getId() + " is out of stock");
                    }
                    product.setStock(remainStock);
                });

                // 5. Lưu xuống DB
                productRepository.saveAll(products);
            } else {
                throw new RuntimeException("Server busy, please try again later");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Process interrupted");
        } finally {
            // 6. Giải phóng lock
            log.info("Waiting for unlock [{}]", lockKey);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Unlock success for [{}]", lockKey);
            }
        }
    }

    @Override
    @Cacheable(value = "product_search", key = "#productFilter.ids")
    public List<Product> search(ProductFilter productFilter) {
        if (productFilter == null || productFilter.getIds() == null || productFilter.getIds().isEmpty()) {
            return List.of();
        }
        return productRepository.findByIdIn(productFilter.getIds());
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::enrichProductResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue().stream()
                .map(this::enrichProductResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(String id) {
        return productRepository.findById(id)
                .map(this::enrichProductResponse)
                .orElse(null);
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(this::enrichProductResponse)
                .orElse(null);
    }

    @Override
    public List<ProductVariantResponse> getVariantsByIds(List<String> variantIds) {
        return productVariantRepository.findAllById(variantIds).stream()
                .map(variant -> {
                    ProductVariantResponse res = productMapper.toProductVariantResponse(variant);
                    int stock = (variant.getInventory() != null && variant.getInventory().getQuantityInStock() != null)
                            ? variant.getInventory().getQuantityInStock()
                            : 0;
                    res.setStockQuantity(stock);
                    res.setInStock(stock > 0);
                    return res;
                })
                .toList();
    }

    private ProductResponse enrichProductResponse(Product product) {
        ProductResponse response = productMapper.toProductResponse(product);
        if (product == null) {
            return response;
        }

        if (product.getPrice() != null && response.getPrice() == null) {
            response.setPrice(product.getPrice());
        }

        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            int currentStock = product.getStock() != null ? product.getStock() : 0;
            response.setStockQuantity(currentStock);
            response.setInStock(currentStock > 0);
            return response;
        }

        ProductVariant firstVariant = product.getVariants().get(0);
        response.setDefaultVariantId(firstVariant.getId());
        if (response.getPrice() == null) {
            response.setPrice(firstVariant.getPrice());
        }
        response.setOriginalPrice(firstVariant.getOriginalPrice());

        if (response.getImageUrl() == null || response.getImageUrl().isEmpty()) {
            response.setImageUrl(firstVariant.getImageUrl());
        }

        int totalStock = 0;
        if (response.getVariants() != null) {
            for (int i = 0; i < product.getVariants().size(); i++) {
                ProductVariant variant = product.getVariants().get(i);
                ProductVariantResponse variantResponse = response.getVariants().get(i);
                int stock = (variant.getInventory() != null && variant.getInventory().getQuantityInStock() != null)
                        ? variant.getInventory().getQuantityInStock()
                        : 0;
                variantResponse.setStockQuantity(stock);
                variantResponse.setInStock(stock > 0);
                totalStock += stock;
            }
        }

        response.setStockQuantity(totalStock);
        response.setInStock(totalStock > 0);
        return response;
    }
}
