package com.restapi.productservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.productservice.consumer.dto.OrderCreatedEvent;
import com.restapi.productservice.dto.request.LockProductItem;
import com.restapi.productservice.dto.request.LockProductReq;
import com.restapi.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "order_created")
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    // retry blocking
    // retry non-blocking => vứt message lỗi sang 1 topic_retry => retry 4 lần vẫn fail => ném message lỗi vào dlt
    public void handleOrderCreatedEvent(String orderString) throws JsonProcessingException {
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(orderString, OrderCreatedEvent.class);
        log.info("Receive order message {}", orderCreatedEvent);

        List<LockProductItem> lockProductItems = new ArrayList<>();

        if (orderCreatedEvent.getOrderItems() != null) {
            orderCreatedEvent.getOrderItems().forEach(orderItem -> {
                LockProductItem lockProductItem = new LockProductItem();
                lockProductItem.setId(orderItem.getProductId());
                lockProductItem.setQuantity(orderItem.getQuantity());

                lockProductItems.add(lockProductItem);
            });
        }

        LockProductReq lockProductReq = new LockProductReq();
        lockProductReq.setItems(lockProductItems);

        productService.lock(lockProductReq);
        log.info("Success to lock product item of {}", orderCreatedEvent.getId());

        // publish message to: product_locked (message: order_id)
        kafkaTemplate.send("product_locked", orderCreatedEvent.getId());
        log.info("Published to product_locked with orderId: {}", orderCreatedEvent.getId());
    }
}
