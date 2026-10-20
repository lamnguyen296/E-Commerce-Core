package com.restapi.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "return_requests",
        indexes = {
                @Index(name = "idx_return_status", columnList = "status"),
                @Index(name = "idx_return_created_at", columnList = "created_at"),
                @Index(name = "idx_return_order_number", columnList = "order_number"),
                @Index(name = "idx_return_customer_name", columnList = "customer_name"),
                @Index(name = "idx_return_deleted", columnList = "deleted")
        }
)
public class ReturnRequest extends BaseEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @Column(name = "order_number", length = 50, nullable = false)
    String orderNumber;

    @Column(name = "customer_name", length = 100, nullable = false)
    String customerName;

    @Column(name = "customer_email", length = 100)
    String customerEmail;

    @Column(name = "reason", length = 255, nullable = false)
    String reason;

    @Builder.Default
    @Column(name = "origin_type", length = 50, nullable = false)
    String originType = "CUSTOMER REQUESTED";

    @Builder.Default
    @Column(name = "status", length = 50, nullable = false)
    String status = "PENDING ACTION";

    @Builder.Default
    @Column(name = "refund_amount", precision = 19, scale = 4)
    BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "product_name", length = 255)
    String productName;

    @Column(name = "carrier", length = 100)
    String carrier;

    @Column(name = "tracking_number", length = 100)
    String trackingNumber;

    @Builder.Default
    @Column(name = "item_count")
    Integer itemCount = 1;

    @Column(name = "note", columnDefinition = "TEXT")
    String note;
}
