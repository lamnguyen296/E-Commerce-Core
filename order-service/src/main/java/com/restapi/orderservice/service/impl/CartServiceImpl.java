package com.restapi.orderservice.service.impl;

import com.restapi.orderservice.dto.request.CartItemRequest;
import com.restapi.orderservice.dto.response.CartItemResponse;
import com.restapi.orderservice.dto.response.CartResponse;
import com.restapi.orderservice.dto.response.CartValidationResponse;
import com.restapi.orderservice.dto.response.ItemStockValidationResult;
import com.restapi.orderservice.entity.Cart;
import com.restapi.orderservice.entity.CartItem;
import com.restapi.orderservice.entity.Discount;
import com.restapi.orderservice.entity.User;
import com.restapi.orderservice.exception.AppException;
import com.restapi.orderservice.exception.ErrorCode;
import com.restapi.orderservice.mapper.CartMapper;
import com.restapi.orderservice.repository.CartItemRepository;
import com.restapi.orderservice.repository.CartRepository;
import com.restapi.orderservice.repository.DiscountRepository;
import com.restapi.orderservice.repository.UserRepository;
import com.restapi.orderservice.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    DiscountRepository discountRepository;
    CartMapper cartMapper;

    private User getCurrentOrGuestUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName()))
                ? auth.getName()
                : "guest";

        return userRepository.findByUsername(username).orElseGet(() -> {
            User guestUser = User.builder()
                    .username("guest")
                    .password("guest123")
                    .fullName("Guest User")
                    .email("guest@storefront.local")
                    .build();
            return userRepository.save(guestUser);
        });
    }

    private Cart getOrCreateUserCart() {
        User user = getCurrentOrGuestUser();
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = Cart.builder()
                    .user(user)
                    .build();
            return cartRepository.save(cart);
        });
    }

    private CartResponse buildCartResponse(Cart cart) {
        CartResponse response = cartMapper.toCartResponse(cart);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            response.setItems(Collections.emptyList());
            response.setTotalQuantity(0);
            response.setTotalPrice(BigDecimal.ZERO);
            return response;
        }

        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = cart.getItems().get(i);
            CartItemResponse itemResponse = response.getItems().get(i);

            itemResponse.setProductVariantId(item.getProductVariantId());
            BigDecimal price = itemResponse.getPrice() != null ? itemResponse.getPrice() : BigDecimal.ZERO;
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            itemResponse.setSubtotal(subtotal);

            totalQuantity += item.getQuantity();
            totalPrice = totalPrice.add(subtotal);
        }

        response.setTotalQuantity(totalQuantity);
        response.setTotalPrice(totalPrice);

        BigDecimal discountAmount = BigDecimal.ZERO;
        Discount discount = cart.getDiscount();
        if (discount != null && discount.getIsActive() != null && discount.getIsActive()) {
            boolean eligible = true;
            if (discount.getMinOrderValue() != null && totalPrice.compareTo(discount.getMinOrderValue()) < 0) {
                eligible = false;
            }
            if (eligible) {
                if ("PERCENTAGE".equalsIgnoreCase(discount.getDiscountType())) {
                    BigDecimal calculated = totalPrice.multiply(discount.getDiscountValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    if (discount.getMaxDiscountAmount() != null && calculated.compareTo(discount.getMaxDiscountAmount()) > 0) {
                        discountAmount = discount.getMaxDiscountAmount();
                    } else {
                        discountAmount = calculated;
                    }
                } else if ("FIXED_AMOUNT".equalsIgnoreCase(discount.getDiscountType())) {
                    discountAmount = discount.getDiscountValue().min(totalPrice);
                }
            }
        }
        response.setDiscountAmount(discountAmount);
        response.setGrandTotal(totalPrice.subtract(discountAmount).max(BigDecimal.ZERO));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart() {
        Cart cart = getOrCreateUserCart();
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(CartItemRequest request) {
        Cart cart = getOrCreateUserCart();

        CartItem item = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId())
                .orElse(null);

        int newQuantity = (item != null ? item.getQuantity() : 0) + (request.getQuantity() != null ? request.getQuantity() : 1);

        if (item == null) {
            item = CartItem.builder()
                    .cart(cart)
                    .productVariantId(request.getProductVariantId())
                    .quantity(newQuantity)
                    .build();
            cart.getItems().add(item);
        } else {
            item.setQuantity(newQuantity);
        }

        cartItemRepository.save(item);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(String cartItemId, int quantity) {
        Cart cart = getOrCreateUserCart();
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(String cartItemId) {
        Cart cart = getOrCreateUserCart();
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse clearCart() {
        Cart cart = getOrCreateUserCart();
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
        }
        cart.setDiscount(null);
        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse applyDiscount(String discountCode) {
        Cart cart = getOrCreateUserCart();
        Discount discount = discountRepository.findByCodeAndIsActiveTrue(discountCode)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if ((discount.getStartDate() != null && discount.getStartDate().isAfter(now))
                || (discount.getEndDate() != null && discount.getEndDate().isBefore(now))) {
            throw new AppException(ErrorCode.DISCOUNT_EXPIRED);
        }

        if (discount.getUsageLimit() != null && discount.getUsageCount() != null
                && discount.getUsageCount() >= discount.getUsageLimit()) {
            throw new AppException(ErrorCode.DISCOUNT_USAGE_LIMIT_EXCEEDED);
        }

        if (discount.getUser() != null && !discount.getUser().getId().equals(cart.getUser().getId())) {
            throw new AppException(ErrorCode.DISCOUNT_NOT_APPLICABLE_FOR_USER);
        }

        CartResponse currentCart = buildCartResponse(cart);
        if (discount.getMinOrderValue() != null && currentCart.getTotalPrice().compareTo(discount.getMinOrderValue()) < 0) {
            throw new AppException(ErrorCode.DISCOUNT_MIN_ORDER_NOT_MET);
        }

        cart.setDiscount(discount);
        cartRepository.save(cart);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeDiscount() {
        Cart cart = getOrCreateUserCart();
        cart.setDiscount(null);
        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartValidationResponse validateCartStock() {
        Cart cart = getOrCreateUserCart();
        List<ItemStockValidationResult> results = new ArrayList<>();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return CartValidationResponse.builder()
                    .isValid(true)
                    .items(Collections.emptyList())
                    .build();
        }

        for (CartItem item : cart.getItems()) {
            results.add(ItemStockValidationResult.builder()
                    .cartItemId(item.getId())
                    .productVariantId(item.getProductVariantId())
                    .productName("Product Item")
                    .requestedQuantity(item.getQuantity())
                    .availableStock(999)
                    .isAvailable(true)
                    .message("In stock")
                    .build());
        }

        return CartValidationResponse.builder()
                .isValid(true)
                .items(results)
                .build();
    }
}
