package com.restapi.orderservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

    String id;
    String status;
    BigDecimal subtotal;
    BigDecimal shippingFee;
    BigDecimal discountAmount;
    BigDecimal grandTotal;
    String shippingName;
    String shippingPhone;
    String shippingProvince;
    String shippingDistrict;
    String shippingWard;
    String shippingStreet;
    String paymentMethod;
    String note;
    LocalDateTime createdAt;
}
