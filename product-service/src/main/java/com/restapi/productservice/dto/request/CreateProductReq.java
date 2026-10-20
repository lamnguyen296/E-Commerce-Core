package com.restapi.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductReq {
    private String id;
    @NotBlank
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String categoryId;
    private String imageUrl;
    private String brand;
}
