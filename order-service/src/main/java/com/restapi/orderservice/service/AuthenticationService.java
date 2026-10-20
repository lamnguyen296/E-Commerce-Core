package com.restapi.orderservice.service;

import java.text.ParseException;

import com.restapi.orderservice.dto.request.AuthenticationRequest;
import com.restapi.orderservice.dto.request.IntrospectRequest;
import com.restapi.orderservice.dto.request.LogoutRequest;
import com.restapi.orderservice.dto.request.RefreshRequest;
import com.restapi.orderservice.dto.response.AuthenticationResponse;
import com.restapi.orderservice.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}

