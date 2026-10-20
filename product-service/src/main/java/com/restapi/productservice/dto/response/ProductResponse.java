package com.restapi.productservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String name;
    String slug;
    String description;
    String brand;
    String imageUrl;
    Boolean featured;
    CategoryResponse category;
    BigDecimal price;
    BigDecimal originalPrice;
    Boolean inStock;
    Integer stockQuantity;
    String defaultVariantId;
    List<ProductVariantResponse> variants;
}
