package com.restapi.orderservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    String id;
    String productVariantId;
    String productId;
    String productName;
    String brand;
    String sku;
    String imageUrl;
    BigDecimal price;
    BigDecimal originalPrice;
    Integer quantity;
    BigDecimal subtotal;
    Integer stockQuantity;
    String attributes;
}
