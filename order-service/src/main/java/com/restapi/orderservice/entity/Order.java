package com.restapi.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @Column(name = "customer_id", length = 50)
    String customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    String status = "NEW";

    @Builder.Default
    @Column(name = "total_amount", precision = 19, scale = 4)
    BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 19, scale = 4)
    BigDecimal subtotal;

    @Column(name = "shipping_fee", precision = 19, scale = 4)
    BigDecimal shippingFee;

    @Builder.Default
    @Column(name = "discount_amount", precision = 19, scale = 4)
    BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "grand_total", precision = 19, scale = 4)
    BigDecimal grandTotal;

    @Column(name = "shipping_name", length = 100)
    String shippingName;

    @Column(name = "shipping_phone", length = 20)
    String shippingPhone;

    @Column(name = "shipping_province", length = 100)
    String shippingProvince;

    @Column(name = "shipping_district", length = 100)
    String shippingDistrict;

    @Column(name = "shipping_ward", length = 100)
    String shippingWard;

    @Column(name = "shipping_street", columnDefinition = "TEXT")
    String shippingStreet;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    @JsonIgnore
    Discount discount;

    @Column(name = "note", columnDefinition = "TEXT")
    String note;
}
