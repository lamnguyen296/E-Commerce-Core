package com.restapi.orderservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartValidationResponse {

    Boolean isValid;
    List<ItemStockValidationResult> items;
}
