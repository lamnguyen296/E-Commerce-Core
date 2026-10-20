package com.restapi.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    Category parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    List<Category> children = new ArrayList<>();

    @Column(name = "name", length = 255, nullable = false)
    String name;

    @Column(name = "slug", length = 255, unique = true, nullable = false)
    String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "image_url", length = 500)
    String imageUrl;

    @Builder.Default
    @Column(name = "featured")
    Boolean featured = false;

    @Column(name = "item_count_label", length = 100)
    String itemCountLabel;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    List<Product> products = new ArrayList<>();
}
