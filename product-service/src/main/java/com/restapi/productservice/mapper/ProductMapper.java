package com.restapi.productservice.mapper;

import com.restapi.productservice.dto.response.ProductResponse;
import com.restapi.productservice.dto.response.ProductVariantResponse;
import com.restapi.productservice.entity.Product;
import com.restapi.productservice.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    @Mapping(target = "stockQuantity", ignore = true)
    @Mapping(target = "inStock", ignore = true)
    ProductVariantResponse toProductVariantResponse(ProductVariant variant);

    @Mapping(target = "price", ignore = true)
    @Mapping(target = "originalPrice", ignore = true)
    @Mapping(target = "inStock", ignore = true)
    @Mapping(target = "stockQuantity", ignore = true)
    @Mapping(target = "defaultVariantId", ignore = true)
    ProductResponse toProductResponse(Product product);
}
