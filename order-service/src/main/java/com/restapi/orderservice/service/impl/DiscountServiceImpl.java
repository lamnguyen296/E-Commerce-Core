package com.restapi.orderservice.service.impl;

import com.restapi.orderservice.dto.response.DiscountResponse;
import com.restapi.orderservice.entity.Discount;
import com.restapi.orderservice.entity.User;
import com.restapi.orderservice.mapper.DiscountMapper;
import com.restapi.orderservice.repository.DiscountRepository;
import com.restapi.orderservice.repository.UserRepository;
import com.restapi.orderservice.service.DiscountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountServiceImpl implements DiscountService {

    DiscountRepository discountRepository;
    UserRepository userRepository;
    DiscountMapper discountMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getAvailableDiscounts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName()))
                ? auth.getName()
                : "guest";

        User user = userRepository.findByUsername(username).orElse(null);
        String userId = user != null ? user.getId() : "";

        List<Discount> discounts = discountRepository.findAvailableDiscountsForUser(userId, LocalDateTime.now());
        if (discounts == null || discounts.isEmpty()) {
            return Collections.emptyList();
        }

        return discounts.stream()
                .map(discountMapper::toDiscountResponse)
                .collect(Collectors.toList());
    }
}
