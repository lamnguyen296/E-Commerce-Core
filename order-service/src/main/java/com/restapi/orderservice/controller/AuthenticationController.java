package com.restapi.orderservice.controller;



import com.restapi.orderservice.dto.request.AuthenticationRequest;
import com.restapi.orderservice.dto.request.IntrospectRequest;
import com.restapi.orderservice.dto.request.LogoutRequest;
import com.restapi.orderservice.dto.request.RefreshRequest;
import com.restapi.orderservice.dto.response.AuthenticationResponse;
import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.dto.response.IntrospectResponse;
import com.restapi.orderservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping({"/token", "/login"})
    BaseResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return BaseResponse.ofSuccess(result);
    }

    @PostMapping("/introspect")
    BaseResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return BaseResponse.ofSuccess(result);
    }

    @PostMapping("/refresh")
    BaseResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return BaseResponse.ofSuccess(result);
    }

    @PostMapping("/logout")
    BaseResponse<Void> logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return BaseResponse.ofSuccess((Void) null);
    }
}