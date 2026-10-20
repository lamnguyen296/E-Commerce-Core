package com.restapi.orderservice.mapper;


import com.restapi.orderservice.dto.request.PermissionRequest;
import com.restapi.orderservice.dto.response.PermissionResponse;
import com.restapi.orderservice.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}