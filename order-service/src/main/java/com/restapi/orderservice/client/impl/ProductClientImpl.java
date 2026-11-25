package com.restapi.orderservice.client.impl;

import com.restapi.orderservice.client.ProductClient;
import com.restapi.orderservice.dto.client.ProductDTO;
import com.restapi.orderservice.dto.client.ProductFilter;
import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.exception.AppException;
import com.restapi.orderservice.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {

    private final WebClient webClient;

    @Override
    public List<ProductDTO> getProductsByIds(ProductFilter productFilter) {
        if (productFilter == null || productFilter.getIds() == null || productFilter.getIds().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            BaseResponse<List<ProductDTO>> response = webClient.post()
                    .uri("http://product-service/ecommerce/v1/products/search")
                    .bodyValue(productFilter)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<BaseResponse<List<ProductDTO>>>() {})
                    .block();

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            return Collections.emptyList();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling product-service: {}", e.getMessage());
            throw new RuntimeException("Không thể lấy thông tin sản phẩm từ Product Service: " + e.getMessage(), e);
        }
    }
}
