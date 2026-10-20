package com.restapi.orderservice.controller;


import com.restapi.orderservice.dto.request.PermissionRequest;
import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.dto.response.PermissionResponse;
import com.restapi.orderservice.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    BaseResponse<PermissionResponse> create(@RequestBody PermissionRequest request){
        return BaseResponse.ofSuccess(permissionService.create(request));
    }

    @GetMapping
    BaseResponse<List<PermissionResponse>> getAll(){
        return BaseResponse.ofSuccess(permissionService.getAll());
    }

    @DeleteMapping("/{permission}")
    BaseResponse<Void> delete(@PathVariable String permission){
        permissionService.delete(permission);
        return BaseResponse.ofDeleteSuccess();
    }
}