package com.restapi.productservice.service;

import com.restapi.productservice.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getFeaturedCategories();
    CategoryResponse getCategoryBySlug(String slug);
}
