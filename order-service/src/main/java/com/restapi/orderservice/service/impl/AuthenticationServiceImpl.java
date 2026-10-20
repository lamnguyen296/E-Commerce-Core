package com.restapi.orderservice.service.impl;

import com.restapi.orderservice.service.*;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restapi.orderservice.dto.request.AuthenticationRequest;
import com.restapi.orderservice.dto.request.IntrospectRequest;
import com.restapi.orderservice.dto.request.LogoutRequest;
import com.restapi.orderservice.dto.request.RefreshRequest;
import com.restapi.orderservice.dto.response.AuthenticationResponse;
import com.restapi.orderservice.dto.response.IntrospectResponse;
import com.restapi.orderservice.exception.AppException;
import com.restapi.orderservice.exception.ErrorCode;
import com.restapi.orderservice.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    JwtService jwtService;
    TokenStorageService tokenStorageService;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getAccessToken();
        boolean isValid = true;

        try {
            verifyTokenFully(token);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        String familyId = UUID.randomUUID().toString();
        var token = jwtService.generateToken(user, familyId);
        
        String refreshToken = UUID.randomUUID().toString();
        tokenStorageService.saveRefreshToken(familyId, user.getUsername(), refreshToken);

        return AuthenticationResponse.builder().accessToken(token).refreshToken(refreshToken).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyTokenFully(request.getAccessToken());

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            long remainingTime = expiryTime.getTime() - new Date().getTime();
            tokenStorageService.blacklistAccessToken(jit, remainingTime);

            String familyId = signToken.getJWTClaimsSet().getStringClaim("fid");
            tokenStorageService.invalidateRefreshTokenFamily(familyId);

        } catch (AppException exception){
            log.info("Access Token already expired or invalid");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        String reqRefreshToken = request.getRefreshToken();
        
        if (reqRefreshToken == null || reqRefreshToken.isEmpty()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        String rtVal = tokenStorageService.getRefreshTokenData(reqRefreshToken);
        if (rtVal == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        String[] parts = rtVal.split(":");
        if (parts.length != 2) {
             throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String familyId = parts[0];
        String username = parts[1];
        
        String currentFamilyRt = tokenStorageService.getCurrentFamilyRefreshToken(familyId);
        
        if (!reqRefreshToken.equals(currentFamilyRt)) {
            // Reuse detected!
            tokenStorageService.invalidateRefreshTokenFamily(familyId);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        
        var token = jwtService.generateToken(user, familyId);
        String newRefreshToken = UUID.randomUUID().toString();
        
        tokenStorageService.saveRefreshToken(familyId, username, newRefreshToken);

        return AuthenticationResponse.builder().accessToken(token).refreshToken(newRefreshToken).authenticated(true).build();
    }

    private SignedJWT verifyTokenFully(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = jwtService.verifyTokenSignatureAndExpiry(token);

        if (tokenStorageService.isAccessTokenBlacklisted(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
}
