package com.restapi.orderservice.mapper;

import com.restapi.orderservice.dto.response.DiscountResponse;
import com.restapi.orderservice.entity.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    DiscountResponse toDiscountResponse(Discount discount);
}
