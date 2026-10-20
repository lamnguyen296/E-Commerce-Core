package com.restapi.orderservice.service;

import com.restapi.orderservice.dto.request.OrderCreationRequest;
import com.restapi.orderservice.dto.request.OrderDTO;
import com.restapi.orderservice.dto.response.OrderResponse;
import com.restapi.orderservice.entity.Order;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO);
    OrderResponse createOrder(OrderCreationRequest request);
    OrderResponse getOrderById(String orderId);
    void updateOrderStatus(String orderId, String status);
}

