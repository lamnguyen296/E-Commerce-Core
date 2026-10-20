package com.restapi.productservice.controller;

import com.restapi.productservice.dto.response.BaseResponse;
import com.restapi.productservice.dto.response.CategoryResponse;
import com.restapi.productservice.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @GetMapping
    public BaseResponse<List<CategoryResponse>> getAllCategories() {
        return BaseResponse.ofSuccess(categoryService.getAllCategories());
    }

    @GetMapping("/featured")
    public BaseResponse<List<CategoryResponse>> getFeaturedCategories() {
        return BaseResponse.ofSuccess(categoryService.getFeaturedCategories());
    }

    @GetMapping("/{slug}")
    public BaseResponse<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        return BaseResponse.ofSuccess(categoryService.getCategoryBySlug(slug));
    }
}
