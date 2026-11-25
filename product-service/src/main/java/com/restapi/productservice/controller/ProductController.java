package com.restapi.productservice.controller;

import com.restapi.productservice.dto.request.CreateProductReq;
import com.restapi.productservice.dto.request.LockProductReq;
import com.restapi.productservice.dto.request.ProductFilter;
import com.restapi.productservice.dto.request.StockReservationRequest;
import com.restapi.productservice.dto.request.UpdateProductReq;
import com.restapi.productservice.dto.response.BaseResponse;
import com.restapi.productservice.dto.response.ProductResponse;
import com.restapi.productservice.dto.response.ProductVariantResponse;
import com.restapi.productservice.entity.Product;
import com.restapi.productservice.service.InventoryService;
import com.restapi.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/v1/products", "/products"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;
    InventoryService inventoryService;
    Environment environment;

    @PostMapping
    public ResponseEntity<BaseResponse<Product>> create(@RequestBody @Valid CreateProductReq createProductReq) {
        log.info("Receive request creating product [{}]", createProductReq);
        return ResponseEntity.ok(new BaseResponse<>(productService.create(createProductReq), "success"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Product>> update(@PathVariable String id, @RequestBody @Valid UpdateProductReq updateProductReq) {
        log.info("Receive request updating product [{}] with data: [{}]", id, updateProductReq);
        return ResponseEntity.ok(new BaseResponse<>(productService.update(id, updateProductReq), "success"));
    }

    @PutMapping("/lock")
    public ResponseEntity<BaseResponse<Boolean>> create(@RequestBody @Valid LockProductReq lockProductReq) {
        log.info("Receive request locking product items: [{}]", lockProductReq.getItems());
        productService.lock(lockProductReq);
        return ResponseEntity.ok(new BaseResponse<>(true, "success"));
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<List<Product>>> search(@RequestBody ProductFilter productFilter) {
        log.info("Receive search products request with filter: [{}] on PORT: {}", productFilter, environment.getProperty("server.port"));
        List<Product> products = productService.search(productFilter);
        log.info("Search products result: found [{}] products", products.size());
        return ResponseEntity.ok(new BaseResponse<>(products, "success"));
    }

    @GetMapping
    public BaseResponse<List<ProductResponse>> getAllProducts() {
        log.info("Receive request get all products");
        List<ProductResponse> products = productService.getAllProducts();
        log.info("Get all products completed, total: [{}] products", products.size());
        return BaseResponse.ofSuccess(products);
    }

    @GetMapping("/featured")
    public BaseResponse<List<ProductResponse>> getFeaturedProducts() {
        log.info("Receive request get featured products");
        return BaseResponse.ofSuccess(productService.getFeaturedProducts());
    }

    @GetMapping("/{id}")
    public BaseResponse<Product> getProductById(@PathVariable String id) {
        log.info("Receive request get product by id [{}]", id);
        return BaseResponse.ofSuccess(productService.getProductById(id));
    }

    @PostMapping("/variants/batch")
    public BaseResponse<List<ProductVariantResponse>> getVariantsByIds(@RequestBody List<String> variantIds) {
        return BaseResponse.ofSuccess(productService.getVariantsByIds(variantIds));
    }

    @GetMapping("/inventory/check")
    public BaseResponse<Boolean> checkStock(@RequestParam String variantId, @RequestParam int quantity) {
        return BaseResponse.ofSuccess(inventoryService.checkStock(variantId, quantity));
    }

    @PostMapping("/inventory/reserve")
    public BaseResponse<String> reserveStock(@RequestBody List<StockReservationRequest> requests) {
        inventoryService.reserveStock(requests);
        return BaseResponse.ofSuccessDataMessage("Stock reserved successfully");
    }

    @PostMapping("/inventory/release")
    public BaseResponse<String> releaseStock(@RequestBody List<StockReservationRequest> requests) {
        inventoryService.releaseStock(requests);
        return BaseResponse.ofSuccessDataMessage("Stock released successfully");
    }
}
