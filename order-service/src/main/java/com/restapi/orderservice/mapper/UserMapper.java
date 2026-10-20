package com.restapi.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.restapi.orderservice.dto.request.UserCreationRequest;
import com.restapi.orderservice.dto.request.UserUpdateRequest;
import com.restapi.orderservice.dto.response.UserResponse;
import com.restapi.orderservice.entity.User;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}