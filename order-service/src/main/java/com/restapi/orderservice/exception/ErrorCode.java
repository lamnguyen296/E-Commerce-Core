package com.restapi.orderservice.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(1009, "Cart not found", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(1010, "Cart item not found", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(1011, "Product variant is out of stock", HttpStatus.BAD_REQUEST),
    DISCOUNT_NOT_FOUND(1012, "Discount code not found", HttpStatus.NOT_FOUND),
    DISCOUNT_EXPIRED(1013, "Discount code has expired", HttpStatus.BAD_REQUEST),
    DISCOUNT_MIN_ORDER_NOT_MET(1014, "Order value does not meet discount minimum requirement", HttpStatus.BAD_REQUEST),
    EMPTY_CART(1015, "Cart is empty, cannot place order", HttpStatus.BAD_REQUEST),
    SHIPPING_ADDRESS_REQUIRED(1016, "Shipping address is required", HttpStatus.BAD_REQUEST),
    DISCOUNT_USAGE_LIMIT_EXCEEDED(1017, "Discount code has reached its usage limit", HttpStatus.BAD_REQUEST),
    DISCOUNT_NOT_APPLICABLE_FOR_USER(1018, "This discount code is not applicable for your account", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1019, "Order not found", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}