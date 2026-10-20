package com.restapi.productservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponse {
    String id;
    String sku;
    BigDecimal price;
    BigDecimal originalPrice;
    String imageUrl;
    String attributes;
    Integer stockQuantity;
    Boolean inStock;
}
