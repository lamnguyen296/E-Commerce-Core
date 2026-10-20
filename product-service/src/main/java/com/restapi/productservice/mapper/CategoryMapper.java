package com.restapi.productservice.mapper;

import com.restapi.productservice.dto.response.CategoryResponse;
import com.restapi.productservice.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}
