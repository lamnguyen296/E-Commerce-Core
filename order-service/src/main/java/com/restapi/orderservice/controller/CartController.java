package com.restapi.orderservice.controller;

import com.restapi.orderservice.dto.request.ApplyDiscountRequest;
import com.restapi.orderservice.dto.request.CartItemRequest;
import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.dto.response.CartResponse;
import com.restapi.orderservice.dto.response.CartValidationResponse;
import com.restapi.orderservice.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    @GetMapping
    public BaseResponse<CartResponse> getCart() {
        return BaseResponse.ofSuccess(cartService.getCart());
    }

    @PostMapping("/items")
    public BaseResponse<CartResponse> addToCart(@RequestBody @Valid CartItemRequest request) {
        return BaseResponse.ofSuccess(cartService.addToCart(request));
    }

    @PutMapping("/items/{itemId}")
    public BaseResponse<CartResponse> updateQuantity(
            @PathVariable String itemId,
            @RequestParam int quantity) {
        return BaseResponse.ofSuccess(cartService.updateCartItemQuantity(itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public BaseResponse<CartResponse> removeItem(@PathVariable String itemId) {
        return BaseResponse.ofSuccess(cartService.removeCartItem(itemId));
    }

    @DeleteMapping("/clear")
    public BaseResponse<CartResponse> clearCart() {
        return BaseResponse.ofSuccess(cartService.clearCart());
    }

    @PostMapping("/discount")
    public BaseResponse<CartResponse> applyDiscount(@RequestBody @Valid ApplyDiscountRequest request) {
        return BaseResponse.ofSuccess(cartService.applyDiscount(request.getDiscountCode()));
    }

    @DeleteMapping("/discount")
    public BaseResponse<CartResponse> removeDiscount() {
        return BaseResponse.ofSuccess(cartService.removeDiscount());
    }

    @GetMapping("/validate-stock")
    public BaseResponse<CartValidationResponse> validateStock() {
        return BaseResponse.ofSuccess(cartService.validateCartStock());
    }
}
