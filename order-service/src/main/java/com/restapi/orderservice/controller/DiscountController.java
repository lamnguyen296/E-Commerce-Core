package com.restapi.orderservice.controller;

import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.dto.response.DiscountResponse;
import com.restapi.orderservice.service.DiscountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountController {

    DiscountService discountService;

    @GetMapping("/my-discounts")
    public BaseResponse<List<DiscountResponse>> getMyDiscounts() {
        return BaseResponse.ofSuccess(discountService.getAvailableDiscounts());
    }
}
