package com.restapi.productservice.service;

import com.restapi.productservice.dto.request.CreateProductReq;
import com.restapi.productservice.dto.request.LockProductReq;
import com.restapi.productservice.dto.request.ProductFilter;
import com.restapi.productservice.dto.request.UpdateProductReq;
import com.restapi.productservice.dto.response.ProductResponse;
import com.restapi.productservice.dto.response.ProductVariantResponse;
import com.restapi.productservice.entity.Product;

import java.util.List;

public interface ProductService {
    Product create(CreateProductReq createProductReq);
    Product update(String id, UpdateProductReq updateProductReq);
    void lock(LockProductReq lockProductReq);
    List<Product> search(ProductFilter productFilter);

    List<ProductResponse> getAllProducts();
    List<ProductResponse> getFeaturedProducts();
    Product getProductById(String id);
    ProductResponse getProductBySlug(String slug);
    List<ProductVariantResponse> getVariantsByIds(List<String> variantIds);
}
