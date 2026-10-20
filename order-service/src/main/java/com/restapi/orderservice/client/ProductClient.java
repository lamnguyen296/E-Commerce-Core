package com.restapi.orderservice.client;

import com.restapi.orderservice.dto.client.ProductDTO;
import com.restapi.orderservice.dto.client.ProductFilter;

import java.util.List;

public interface ProductClient {
    List<ProductDTO> getProductsByIds(ProductFilter productFilter);
}
