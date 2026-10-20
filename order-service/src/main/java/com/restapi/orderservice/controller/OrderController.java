package com.restapi.orderservice.controller;

import com.restapi.orderservice.dto.request.OrderCreationRequest;
import com.restapi.orderservice.dto.request.OrderDTO;
import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.dto.response.OrderResponse;
import com.restapi.orderservice.entity.Order;
import com.restapi.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping({"/v1/orders", "/orders"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    /**
     * API đặt hàng khớp 100% với bài giảng (Ảnh 1)
     */
    @PostMapping
    public ResponseEntity<BaseResponse<Order>> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok().body(new BaseResponse<>(orderService.createOrder(orderDTO),
                "Create order successfully !", null));
    }

    @PostMapping("/checkout")
    public BaseResponse<OrderResponse> checkout(@RequestBody @Valid OrderCreationRequest request) {
        return BaseResponse.ofSuccess(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    public BaseResponse<OrderResponse> getOrderById(@PathVariable String orderId) {
        return BaseResponse.ofSuccess(orderService.getOrderById(orderId));
    }
}
