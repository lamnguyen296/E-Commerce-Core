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
@Table(name = "shipping_rates")
public class ShippingRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id", nullable = false)
    Carrier carrier;

    @Column(name = "region", length = 100, nullable = false)
    String region;

    @Column(name = "weight_up_to", precision = 8, scale = 2)
    BigDecimal weightUpTo;

    @Column(name = "fee", precision = 19, scale = 4, nullable = false)
    BigDecimal fee;
}
