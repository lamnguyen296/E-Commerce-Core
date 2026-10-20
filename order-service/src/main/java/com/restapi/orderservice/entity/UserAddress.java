package com.restapi.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_addresses")
public class UserAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "receiver_name", length = 100, nullable = false)
    String receiverName;

    @Column(name = "phone", length = 20, nullable = false)
    String phone;

    @Column(name = "province", length = 100, nullable = false)
    String province;

    @Column(name = "district", length = 100, nullable = false)
    String district;

    @Column(name = "ward", length = 100, nullable = false)
    String ward;

    @Column(name = "street_address", columnDefinition = "TEXT", nullable = false)
    String streetAddress;

    @Builder.Default
    @Column(name = "is_default")
    Boolean isDefault = false;
}
