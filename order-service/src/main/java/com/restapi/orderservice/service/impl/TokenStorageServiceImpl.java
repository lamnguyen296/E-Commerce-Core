package com.restapi.orderservice.service.impl;

import com.restapi.orderservice.service.*;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenStorageServiceImpl implements TokenStorageService {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public void saveRefreshToken(String familyId, String username, String refreshToken) {
        try {
            stringRedisTemplate.opsForValue().set("rt_family:" + familyId, refreshToken, REFRESHABLE_DURATION, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue().set("rt:" + refreshToken, familyId + ":" + username, REFRESHABLE_DURATION, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis is not available, skipped saving refresh token in Redis: {}", e.getMessage());
        }
    }

    public void blacklistAccessToken(String jit, long remainingTimeMs) {
        try {
            if (remainingTimeMs > 0) {
                stringRedisTemplate.opsForValue().set("bl_at:" + jit, "invalid", remainingTimeMs, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            log.warn("Redis is not available, skipped blacklisting token: {}", e.getMessage());
        }
    }

    public boolean isAccessTokenBlacklisted(String jit) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey("bl_at:" + jit));
        } catch (Exception e) {
            log.warn("Redis is not available, skipped checking blacklist: {}", e.getMessage());
            return false;
        }
    }

    public void invalidateRefreshTokenFamily(String familyId) {
        try {
            if (familyId != null && !familyId.isEmpty()) {
                stringRedisTemplate.delete("rt_family:" + familyId);
            }
        } catch (Exception e) {
            log.warn("Redis is not available, skipped invalidating token family: {}", e.getMessage());
        }
    }

    public String getRefreshTokenData(String refreshToken) {
        try {
            return stringRedisTemplate.opsForValue().get("rt:" + refreshToken);
        } catch (Exception e) {
            log.warn("Redis is not available: {}", e.getMessage());
            return null;
        }
    }

    public String getCurrentFamilyRefreshToken(String familyId) {
        try {
            return stringRedisTemplate.opsForValue().get("rt_family:" + familyId);
        } catch (Exception e) {
            log.warn("Redis is not available: {}", e.getMessage());
            return null;
        }
    }
}
