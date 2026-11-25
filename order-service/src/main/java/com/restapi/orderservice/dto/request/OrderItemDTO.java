package com.restapi.orderservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    @NotEmpty(message = "ProductId không được để trống")
    private String productId;

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;
}
