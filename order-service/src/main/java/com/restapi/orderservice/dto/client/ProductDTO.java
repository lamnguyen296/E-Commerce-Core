package com.restapi.orderservice.dto.client;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String categoryId;
}
