package com.restapi.orderservice.controller;

import com.restapi.orderservice.dto.request.UserCreationRequest;
import com.restapi.orderservice.dto.request.UserUpdateRequest;
import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.dto.response.UserResponse;
import com.restapi.orderservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    BaseResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        return BaseResponse.ofSuccess(userService.createUser(request));
    }

    @GetMapping
    BaseResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return BaseResponse.ofSuccess(userService.getUsers());
    }

    @GetMapping("/{userId}")
    BaseResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return BaseResponse.ofSuccess(userService.getUser(userId));
    }

    @GetMapping("/myInfo")
    BaseResponse<UserResponse> getMyInfo(){
        return BaseResponse.ofSuccess(userService.getMyInfo());
    }

    @DeleteMapping("/{userId}")
    BaseResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return BaseResponse.ofSuccessDataMessage("User has been deleted");
    }

    @PutMapping("/{userId}")
    BaseResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return BaseResponse.ofSuccess(userService.updateUser(userId, request));
    }
}