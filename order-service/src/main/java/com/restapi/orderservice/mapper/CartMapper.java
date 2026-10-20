package com.restapi.orderservice.mapper;

import com.restapi.orderservice.dto.response.CartItemResponse;
import com.restapi.orderservice.dto.response.CartResponse;
import com.restapi.orderservice.entity.Cart;
import com.restapi.orderservice.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DiscountMapper.class})
public interface CartMapper {

    @Mapping(target = "productVariantId", source = "productVariantId")
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "originalPrice", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "stockQuantity", ignore = true)
    CartItemResponse toCartItemResponse(CartItem item);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "totalQuantity", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "grandTotal", ignore = true)
    @Mapping(target = "appliedDiscount", source = "discount")
    CartResponse toCartResponse(Cart cart);
}
