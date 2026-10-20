package com.restapi.orderservice.service;

import com.restapi.orderservice.dto.request.UserCreationRequest;
import com.restapi.orderservice.dto.request.UserUpdateRequest;
import com.restapi.orderservice.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
    UserResponse getMyInfo();
    UserResponse updateUser(String userId, UserUpdateRequest request);
    void deleteUser(String userId);
    List<UserResponse> getUsers();
    UserResponse getUser(String id);
}

