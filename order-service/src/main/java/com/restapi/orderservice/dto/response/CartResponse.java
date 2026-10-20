package com.restapi.orderservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    String id;
    String userId;
    List<CartItemResponse> items;
    Integer totalQuantity;
    BigDecimal totalPrice;
    BigDecimal discountAmount;
    BigDecimal grandTotal;
    DiscountResponse appliedDiscount;
}
