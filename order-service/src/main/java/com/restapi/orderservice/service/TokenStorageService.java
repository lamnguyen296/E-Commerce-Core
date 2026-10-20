package com.restapi.orderservice.service;

public interface TokenStorageService {
    void saveRefreshToken(String familyId, String username, String refreshToken);
    void blacklistAccessToken(String jit, long remainingTimeMs);
    boolean isAccessTokenBlacklisted(String jit);
    void invalidateRefreshTokenFamily(String familyId);
    String getRefreshTokenData(String refreshToken);
    String getCurrentFamilyRefreshToken(String familyId);
}

