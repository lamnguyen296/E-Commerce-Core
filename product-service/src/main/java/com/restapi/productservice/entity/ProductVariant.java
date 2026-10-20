package com.restapi.productservice.entity;

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
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "sku", length = 50, unique = true, nullable = false)
    String sku;

    @Column(name = "price", precision = 19, scale = 4, nullable = false)
    BigDecimal price;

    @Column(name = "original_price", precision = 19, scale = 4)
    BigDecimal originalPrice;

    @Column(name = "image_url", length = 500)
    String imageUrl;

    @Column(name = "attributes", columnDefinition = "json")
    String attributes;

    @OneToOne(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    Inventory inventory;
}
