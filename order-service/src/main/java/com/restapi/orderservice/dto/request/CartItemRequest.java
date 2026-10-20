package com.restapi.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequest {
    @NotBlank(message = "Product variant ID is required")
    String productVariantId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Builder.Default
    Integer quantity = 1;
}
