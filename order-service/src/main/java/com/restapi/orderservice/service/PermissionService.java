package com.restapi.orderservice.service;

import com.restapi.orderservice.dto.request.PermissionRequest;
import com.restapi.orderservice.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);
    List<PermissionResponse> getAll();
    void delete(String permission);
}

