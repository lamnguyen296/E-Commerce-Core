package com.restapi.orderservice.configuration;

import com.restapi.orderservice.entity.*;
import com.restapi.orderservice.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            UserAddressRepository userAddressRepository) {

        return args -> {
            // 1. Tạo tài khoản admin nếu chưa có
            User admin = userRepository.findByUsername("admin").orElseGet(() -> {
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .fullName("System Administrator")
                        .email("admin@example.com")
                        .phone("0987654321")
                        .build();
                user = userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
                return user;
            });

            // Tạo địa chỉ mẫu cho admin nếu chưa có
            if (userAddressRepository.findByUserId(admin.getId()).isEmpty()) {
                userAddressRepository.save(UserAddress.builder()
                        .user(admin)
                        .receiverName("Admin User")
                        .phone("0987654321")
                        .province("Hà Nội")
                        .district("Cầu Giấy")
                        .ward("Dịch Vọng Hậu")
                        .streetAddress("Số 1 Đại Cồ Việt")
                        .isDefault(true)
                        .build());
            }

            // 2. Tạo tài khoản customer nếu chưa có
            User customer = userRepository.findByUsername("customer").orElseGet(() -> {
                User user = User.builder()
                        .username("customer")
                        .password(passwordEncoder.encode("123"))
                        .fullName("Customer Test")
                        .email("customer@example.com")
                        .phone("0912345678")
                        .build();
                user = userRepository.save(user);
                log.warn("customer user has been created with default password: 123");
                return user;
            });

            // Tạo địa chỉ mẫu cho customer nếu chưa có
            if (userAddressRepository.findByUserId(customer.getId()).isEmpty()) {
                userAddressRepository.save(UserAddress.builder()
                        .user(customer)
                        .receiverName("Customer Test")
                        .phone("0912345678")
                        .province("Hồ Chí Minh")
                        .district("Quận 1")
                        .ward("Phường Bến Nghé")
                        .streetAddress("123 Lê Lợi")
                        .isDefault(true)
                        .build());
            }
        };
    }

    @Bean
    ApplicationRunner returnSampleDataInitializer(ReturnRequestRepository returnRequestRepository) {
        return args -> {
            if (returnRequestRepository.count() == 0) {
                log.info("Khởi tạo dữ liệu mẫu cho yêu cầu trả hàng (Return Requests)...");
                var sampleData = java.util.List.of(
                        ReturnRequest.builder()
                                .id("#RET-8842")
                                .orderNumber("Order #ORD-9921-X")
                                .customerName("Eleanor Shellstrop")
                                .customerEmail("eleanor@shellstrop.org")
                                .productName("Ergonomic Executive Chair (Midnight Navy)")
                                .reason("Damaged on arrival")
                                .originType("CUSTOMER REQUESTED")
                                .status("IN-TRANSIT")
                                .refundAmount(new BigDecimal("189.50"))
                                .carrier("FedEx Priority")
                                .trackingNumber("TRK-98234-US")
                                .itemCount(2)
                                .createdAt(LocalDateTime.now().minusMinutes(2))
                                .build(),
                        ReturnRequest.builder()
                                .id("#RET-8841")
                                .orderNumber("Order #ORD-4420-Y")
                                .customerName("Chidi Anagonye")
                                .customerEmail("chidi.ethics@univ.edu")
                                .productName("Philosophy & Ethics Hardcover Book Set")
                                .reason("Failed Delivery")
                                .originType("FAILED DELIVERY")
                                .status("PENDING ACTION")
                                .refundAmount(new BigDecimal("74.20"))
                                .carrier("DHL Express")
                                .trackingNumber("DHL-44201-99")
                                .itemCount(1)
                                .createdAt(LocalDateTime.now().minusMinutes(45))
                                .build(),
                        ReturnRequest.builder()
                                .id("#RET-8839")
                                .orderNumber("Order #ORD-1150-Z")
                                .customerName("Tahani Al-Jamil")
                                .customerEmail("tahani.gala@aljamil.co.uk")
                                .productName("Luxury Cashmere Throw & Silk Cushion Set")
                                .reason("Wrong Item Sent")
                                .originType("CUSTOMER REQUESTED")
                                .status("RESTOCKED")
                                .refundAmount(new BigDecimal("890.00"))
                                .carrier("UPS Worldwide")
                                .trackingNumber("1Z9999999999999999")
                                .itemCount(3)
                                .createdAt(LocalDateTime.now().minusHours(2))
                                .build(),
                        ReturnRequest.builder()
                                .id("#RET-8835")
                                .orderNumber("Order #ORD-0082-A")
                                .customerName("Jason Mendoza")
                                .customerEmail("jason.dance@jacksonville.fl")
                                .productName("Portable Bluetooth Party Speaker 500W")
                                .reason("Defective Unit")
                                .originType("CUSTOMER REQUESTED")
                                .status("WAREHOUSE RECEIVED")
                                .refundAmount(new BigDecimal("120.00"))
                                .carrier("USPS Priority")
                                .trackingNumber("9400100000000000000000")
                                .itemCount(1)
                                .createdAt(LocalDateTime.now().minusHours(5))
                                .build(),
                        ReturnRequest.builder()
                                .id("#RET-8830")
                                .orderNumber("Order #ORD-0012-M")
                                .customerName("Michael Realman")
                                .customerEmail("michael@goodplace.architect")
                                .productName("Architectural Drafting Set Deluxe")
                                .reason("Unwanted Gift")
                                .originType("CUSTOMER REQUESTED")
                                .status("REFUNDED")
                                .refundAmount(new BigDecimal("315.00"))
                                .carrier("FedEx Ground")
                                .trackingNumber("TRK-44321-US")
                                .itemCount(1)
                                .createdAt(LocalDateTime.now().minusHours(8))
                                .build(),
                        ReturnRequest.builder()
                                .id("#RET-8828")
                                .orderNumber("Order #ORD-9900-J")
                                .customerName("Janet Subroutine")
                                .customerEmail("janet@void.universe")
                                .productName("Multi-Sensor Smart Home Hub v3")
                                .reason("Defective Unit")
                                .originType("CUSTOMER REQUESTED")
                                .status("PENDING ACTION")
                                .refundAmount(new BigDecimal("450.00"))
                                .carrier("DHL eCommerce")
                                .trackingNumber("DHL-88771-00")
                                .itemCount(4)
                                .createdAt(LocalDateTime.now().minusDays(1))
                                .build()
                );
                returnRequestRepository.saveAll(sampleData);
                log.info("Đã khởi tạo thành công {} yêu cầu trả hàng mẫu.", sampleData.size());
            }
        };
    }
}
