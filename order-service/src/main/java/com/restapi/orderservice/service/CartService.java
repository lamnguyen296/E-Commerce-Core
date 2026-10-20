package com.restapi.orderservice.service;

import com.restapi.orderservice.dto.request.CartItemRequest;
import com.restapi.orderservice.dto.response.CartResponse;
import com.restapi.orderservice.dto.response.CartValidationResponse;

public interface CartService {
    CartResponse getCart();
    CartResponse addToCart(CartItemRequest request);
    CartResponse updateCartItemQuantity(String cartItemId, int quantity);
    CartResponse removeCartItem(String cartItemId);
    CartResponse clearCart();
    CartResponse applyDiscount(String discountCode);
    CartResponse removeDiscount();
    CartValidationResponse validateCartStock();
}
