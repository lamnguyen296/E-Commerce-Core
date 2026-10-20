package com.restapi.orderservice.service;

import com.restapi.orderservice.dto.response.DiscountResponse;

import java.util.List;

public interface DiscountService {
    List<DiscountResponse> getAvailableDiscounts();
}
