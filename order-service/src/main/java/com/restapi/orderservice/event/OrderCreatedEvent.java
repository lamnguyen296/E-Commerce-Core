package com.restapi.orderservice.event;

import com.restapi.orderservice.entity.Order;
import com.restapi.orderservice.entity.OrderItem;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderCreatedEvent extends Order {
    private List<OrderItem> orderItems;
}
