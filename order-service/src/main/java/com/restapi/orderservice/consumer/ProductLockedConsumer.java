package com.restapi.orderservice.consumer;

import com.restapi.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductLockedConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "product_locked")
    public void handleProductLocked(String orderId) {
        log.info("Received product_locked event for orderId: {}", orderId);
        orderService.updateOrderStatus(orderId, "CONFIRMED");
    }
}
