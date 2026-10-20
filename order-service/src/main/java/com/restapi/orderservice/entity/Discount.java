package com.restapi.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "discounts")
public class Discount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @Column(name = "code", length = 50, unique = true, nullable = false)
    String code;

    @Column(name = "name", length = 255, nullable = false)
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "discount_type", length = 20, nullable = false)
    String discountType; // "PERCENTAGE" hoặc "FIXED_AMOUNT"

    @Column(name = "discount_value", precision = 19, scale = 4, nullable = false)
    BigDecimal discountValue;

    @Column(name = "max_discount_amount", precision = 19, scale = 4)
    BigDecimal maxDiscountAmount;

    @Builder.Default
    @Column(name = "min_order_value", precision = 19, scale = 4)
    BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "start_date")
    LocalDateTime startDate;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Column(name = "usage_limit")
    Integer usageLimit;

    @Builder.Default
    @Column(name = "usage_count")
    Integer usageCount = 0;

    @Builder.Default
    @Column(name = "is_active")
    Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user; // NULL = mã dùng chung, có giá trị = mã riêng cho user đó
}
