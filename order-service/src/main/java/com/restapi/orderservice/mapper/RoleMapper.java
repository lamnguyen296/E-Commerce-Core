package com.restapi.orderservice.mapper;

import com.restapi.orderservice.dto.request.RoleRequest;
import com.restapi.orderservice.dto.response.RoleResponse;
import com.restapi.orderservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    @Mapping(target = "rolePermissions", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}