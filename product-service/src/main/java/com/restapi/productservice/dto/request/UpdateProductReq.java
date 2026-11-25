package com.restapi.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductReq {
    @NotBlank
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String categoryId;
    private String imageUrl;
    private String brand;
}

