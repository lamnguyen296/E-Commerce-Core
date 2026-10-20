package com.restapi.orderservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplyDiscountRequest {

    @NotBlank(message = "Discount code cannot be blank")
    String discountCode;
}
