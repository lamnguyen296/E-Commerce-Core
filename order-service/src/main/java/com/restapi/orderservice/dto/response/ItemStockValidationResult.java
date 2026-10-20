package com.restapi.orderservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemStockValidationResult {

    String cartItemId;
    String productVariantId;
    String productName;
    Integer requestedQuantity;
    Integer availableStock;
    Boolean isAvailable;
    String message;
}
