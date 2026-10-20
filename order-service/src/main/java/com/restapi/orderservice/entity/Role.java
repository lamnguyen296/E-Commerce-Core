package com.restapi.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @Column(name = "name", length = 50, unique = true, nullable = false)
    String name;

    @Column(name = "description", length = 255)
    String description;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<RolePermission> rolePermissions = new HashSet<>();

    public Set<Permission> getPermissions() {
        if (rolePermissions == null) {
            return Collections.emptySet();
        }
        return rolePermissions.stream()
                .map(RolePermission::getPermission)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void setPermissions(Set<Permission> permissions) {
        if (this.rolePermissions == null) {
            this.rolePermissions = new HashSet<>();
        } else {
            this.rolePermissions.clear();
        }
        if (permissions != null) {
            for (Permission permission : permissions) {
                RolePermission rp = RolePermission.builder()
                        .role(this)
                        .permission(permission)
                        .build();
                this.rolePermissions.add(rp);
            }
        }
    }
}
