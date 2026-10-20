package com.restapi.orderservice.mapper;

import com.restapi.orderservice.dto.response.OrderResponse;
import com.restapi.orderservice.entity.Order;
import com.restapi.orderservice.event.OrderCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface OrderMapper {

    @Mapping(target = "paymentMethod", ignore = true)
    OrderResponse toOrderResponse(Order order);

    OrderCreatedEvent toEvent(Order order);
}

