package com.restapi.productservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @PrePersist
    public void ensureId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "products"})
    Category category;

    @Column(name = "name", length = 255, nullable = false)
    String name;

    @Column(name = "slug", length = 255, unique = true)
    String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "brand", length = 100)
    String brand;

    @Column(name = "image_url", length = 500)
    String imageUrl;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "stock")
    @Builder.Default
    Integer stock = 0;

    @Builder.Default
    @Column(name = "featured")
    Boolean featured = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    List<ProductVariant> variants = new ArrayList<>();

    public Integer getStockQuantity() {
        return stock != null ? stock : 0;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stock = stockQuantity;
    }

    public String getCategoryId() {
        return category != null ? category.getId() : null;
    }
}
