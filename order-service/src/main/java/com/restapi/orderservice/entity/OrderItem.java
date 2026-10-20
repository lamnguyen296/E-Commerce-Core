package com.restapi.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    Order order;

    @Column(name = "order_ref_id", length = 36)
    String orderId;

    @Column(name = "product_id", length = 36, nullable = false)
    String productId;

    @Column(name = "product_variant_id", length = 36)
    String productVariantId;

    @Column(name = "product_name", length = 255)
    String productName;

    @Column(name = "sku", length = 100)
    String sku;

    @Column(name = "attributes", columnDefinition = "json")
    String attributes;

    @Column(name = "thumbnail_url", length = 500)
    String thumbnailUrl;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "price", precision = 19, scale = 4, nullable = false)
    BigDecimal price;

    @Column(name = "unit_price", precision = 19, scale = 4)
    BigDecimal unitPrice;

    @Column(name = "total_price", precision = 19, scale = 4)
    BigDecimal totalPrice;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        this.unitPrice = price;
    }
}
