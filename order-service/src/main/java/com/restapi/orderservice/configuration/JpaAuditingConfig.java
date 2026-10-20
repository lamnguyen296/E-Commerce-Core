package com.restapi.orderservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // Lấy thông tin user hiện tại từ Spring Security Context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Nếu không có thông tin đăng nhập, chưa xác thực, hoặc là user ẩn danh
            if (authentication == null || 
                !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("system");
            }

            // Trả về username (hoặc ID) của tài khoản đang thực hiện request
            return Optional.of(authentication.getName());
        };
    }
}
