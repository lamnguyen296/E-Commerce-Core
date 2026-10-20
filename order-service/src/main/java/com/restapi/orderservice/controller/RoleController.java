package com.restapi.orderservice.controller;


import com.restapi.orderservice.dto.request.RoleRequest;
import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.dto.response.RoleResponse;
import com.restapi.orderservice.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    BaseResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return BaseResponse.ofSuccess(roleService.create(request));
    }

    @GetMapping
    BaseResponse<List<RoleResponse>> getAll(){
        return BaseResponse.ofSuccess(roleService.getAll());
    }

    @DeleteMapping("/{role}")
    BaseResponse<Void> delete(@PathVariable String role){
        roleService.delete(role);
        return BaseResponse.ofDeleteSuccess();
    }
}