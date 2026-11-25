package com.restapi.orderservice.service.impl;

import com.restapi.orderservice.client.ProductClient;
import com.restapi.orderservice.dto.client.ProductDTO;
import com.restapi.orderservice.dto.client.ProductFilter;
import com.restapi.orderservice.dto.request.OrderCreationRequest;
import com.restapi.orderservice.dto.request.OrderDTO;
import com.restapi.orderservice.dto.request.OrderItemDTO;
import com.restapi.orderservice.dto.response.OrderResponse;
import com.restapi.orderservice.entity.*;
import com.restapi.orderservice.event.OrderCreatedEvent;
import com.restapi.orderservice.exception.AppException;
import com.restapi.orderservice.exception.ErrorCode;
import com.restapi.orderservice.mapper.OrderMapper;
import com.restapi.orderservice.repository.*;
import com.restapi.orderservice.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    PaymentRepository paymentRepository;
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    DiscountRepository discountRepository;
    UserRepository userRepository;
    UserAddressRepository userAddressRepository;
    OrderMapper orderMapper;
    ProductClient productClient;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        // 1. Gom danh sách productIds cần mua (dùng distinct tránh trùng)
        List<String> productIds = orderDTO.getOrderItems().stream()
                .map(OrderItemDTO::getProductId)
                .distinct()
                .toList();
        log.info("Product ids [{}]", productIds);

        // 2. Gọi sang Product Service qua WebClient để lấy thông tin sản phẩm và tồn kho
        List<ProductDTO> products = productClient.getProductsByIds(new ProductFilter(productIds));
        log.info("Finish to send get products from product-service with size [{}]", products.size());

        // 3. Đưa danh sách sản phẩm vào Map để tra cứu O(1)
        Map<String, ProductDTO> productPriceMap = new HashMap<>();
        products.forEach(productDTO -> {
            productPriceMap.put(productDTO.getId(), productDTO);
        });

        // 4. Khởi tạo và lưu Order ban đầu
        Order order = new Order();
        order.setCustomerId(orderDTO.getCustomerId());
        order.setStatus("NEW");
        order.setTotalAmount(BigDecimal.ZERO);
        Order savedOrder = orderRepository.save(order);
        log.info("Created new order [{}]", savedOrder.getId());

        // 5. Duyệt từng item để validate và tính tiền
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            ProductDTO productDTO = productPriceMap.get(itemDTO.getProductId());
            if (productDTO == null) {
                throw new RuntimeException("Product " + itemDTO.getProductId() + " not existed");
            }

            if (itemDTO.getQuantity() > productDTO.getStockQuantity()) {
                throw new RuntimeException("Product " + itemDTO.getProductId() + " not enough");
            }

            BigDecimal price = productDTO.getPrice() != null ? productDTO.getPrice() : BigDecimal.ZERO;
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setOrderId(savedOrder.getId());
            item.setProductId(itemDTO.getProductId());
            item.setProductName(productDTO.getName());
            item.setPrice(price);
            item.setQuantity(itemDTO.getQuantity());
            item.setTotalPrice(price.multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            orderItems.add(item);
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        // 6. Lưu tất cả OrderItems và cập nhật lại tổng tiền vào Order
        var createdOrderItems = orderItemRepository.saveAll(orderItems);
        savedOrder.setTotalAmount(totalAmount);
        savedOrder.setGrandTotal(totalAmount);
        savedOrder.setItems(orderItems);

        Order createdOder = orderRepository.save(savedOrder);

        OrderCreatedEvent orderCreatedEvent = orderMapper.toEvent(createdOder);
        orderCreatedEvent.setOrderItems(createdOrderItems);
        kafkaTemplate.send("order_created", orderCreatedEvent);
        log.info("Publish new order success to order_created");
        return createdOder;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_CART);
        }

        String shippingName = request.getShippingName();
        String shippingPhone = request.getShippingPhone();
        String shippingProvince = request.getShippingProvince();
        String shippingDistrict = request.getShippingDistrict();
        String shippingWard = request.getShippingWard();
        String shippingStreet = request.getShippingStreet();

        if (request.getUserAddressId() != null && !request.getUserAddressId().isBlank()) {
            UserAddress address = userAddressRepository.findById(request.getUserAddressId()).orElse(null);
            if (address != null) {
                shippingName = address.getReceiverName();
                shippingPhone = address.getPhone();
                shippingProvince = address.getProvince();
                shippingDistrict = address.getDistrict();
                shippingWard = address.getWard();
                shippingStreet = address.getStreetAddress();
            }
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            BigDecimal price = BigDecimal.valueOf(100000);
            subtotal = subtotal.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        BigDecimal shippingFee = BigDecimal.valueOf(145000);
        BigDecimal discountAmount = BigDecimal.ZERO;
        Discount discount = cart.getDiscount();

        if (discount != null && Boolean.TRUE.equals(discount.getIsActive())) {
            LocalDateTime now = LocalDateTime.now();
            boolean validDate = (discount.getStartDate() == null || !discount.getStartDate().isAfter(now))
                    && (discount.getEndDate() == null || !discount.getEndDate().isBefore(now));
            boolean validUsage = discount.getUsageLimit() == null || discount.getUsageCount() < discount.getUsageLimit();
            boolean validUser = discount.getUser() == null || discount.getUser().getId().equals(user.getId());
            boolean validMinOrder = discount.getMinOrderValue() == null || subtotal.compareTo(discount.getMinOrderValue()) >= 0;

            if (validDate && validUsage && validUser && validMinOrder) {
                if ("PERCENTAGE".equalsIgnoreCase(discount.getDiscountType())) {
                    BigDecimal calculated = subtotal.multiply(discount.getDiscountValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    if (discount.getMaxDiscountAmount() != null && calculated.compareTo(discount.getMaxDiscountAmount()) > 0) {
                        discountAmount = discount.getMaxDiscountAmount();
                    } else {
                        discountAmount = calculated;
                    }
                } else if ("FIXED_AMOUNT".equalsIgnoreCase(discount.getDiscountType())) {
                    discountAmount = discount.getDiscountValue().min(subtotal);
                }

                discount.setUsageCount((discount.getUsageCount() != null ? discount.getUsageCount() : 0) + 1);
                discountRepository.save(discount);
            } else {
                discount = null;
            }
        }

        BigDecimal grandTotal = subtotal.subtract(discountAmount).add(shippingFee).max(BigDecimal.ZERO);

        Order order = Order.builder()
                .user(user)
                .customerId(user.getId())
                .status("NEW")
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .grandTotal(grandTotal)
                .totalAmount(grandTotal)
                .discount(discount)
                .shippingName(shippingName)
                .shippingPhone(shippingPhone)
                .shippingProvince(shippingProvince)
                .shippingDistrict(shippingDistrict)
                .shippingWard(shippingWard)
                .shippingStreet(shippingStreet)
                .note(request.getNote())
                .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem item : cart.getItems()) {
            BigDecimal unitPrice = BigDecimal.valueOf(100000);
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .orderId(savedOrder.getId())
                    .productId(item.getProductVariantId())
                    .productVariantId(item.getProductVariantId())
                    .productName("Product Variant")
                    .sku("SKU-" + item.getProductVariantId())
                    .quantity(item.getQuantity())
                    .price(unitPrice)
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .build();
            orderItemRepository.save(orderItem);
        }

        String paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : "COD";
        Payment payment = Payment.builder()
                .order(savedOrder)
                .paymentMethod(paymentMethod)
                .amount(grandTotal)
                .status("PENDING")
                .build();
        paymentRepository.save(payment);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setDiscount(null);
        cartRepository.save(cart);

        OrderResponse response = orderMapper.toOrderResponse(savedOrder);
        response.setPaymentMethod(paymentMethod);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            User user = userRepository.findByUsername(auth.getName()).orElse(null);
            if (user != null && order.getUser() != null && !order.getUser().getId().equals(user.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
            log.info("Updated order {} status to {}", orderId, status);
        });
    }
}

