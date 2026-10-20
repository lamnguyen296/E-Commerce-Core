package com.restapi.orderservice.entity;

import jakarta.persistence.*;
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
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Column(name = "transaction_id", length = 100)
    String transactionId;

    @Column(name = "payment_method", length = 20, nullable = false)
    String paymentMethod;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    BigDecimal amount;

    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    String status = "PENDING";
}
