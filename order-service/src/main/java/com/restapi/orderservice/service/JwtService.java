package com.restapi.orderservice.service;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.restapi.orderservice.entity.User;

public interface JwtService {
    String generateToken(User user, String familyId);
    SignedJWT verifyTokenSignatureAndExpiry(String token) throws JOSEException, ParseException;
}

