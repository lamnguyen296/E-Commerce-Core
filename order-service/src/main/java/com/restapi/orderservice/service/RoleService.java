package com.restapi.orderservice.service;

import com.restapi.orderservice.dto.request.RoleRequest;
import com.restapi.orderservice.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);
    List<RoleResponse> getAll();
    void delete(String role);
}

