package com.restapi.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    String username;

    @Column(name = "password_hash", length = 255, nullable = false)
    String passwordHash;

    @Column(name = "email", length = 100, unique = true)
    String email;

    @Column(name = "phone", length = 20)
    String phone;

    @Column(name = "full_name", length = 100)
    String fullName;

    // Transient fields for compatibility with existing DTOs/controllers
    @Transient
    String firstName;

    @Transient
    String lastName;

    @Transient
    LocalDate dob;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<UserAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    List<Order> orders = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Cart cart;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    List<Notification> notifications = new ArrayList<>();

    public String getPassword() {
        return this.passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    public Set<Role> getRoles() {
        if (userRoles == null) {
            return Collections.emptySet();
        }
        return userRoles.stream()
                .map(UserRole::getRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void setRoles(Set<Role> roles) {
        if (this.userRoles == null) {
            this.userRoles = new HashSet<>();
        } else {
            this.userRoles.clear();
        }
        if (roles != null) {
            for (Role role : roles) {
                UserRole ur = UserRole.builder()
                        .user(this)
                        .role(role)
                        .build();
                this.userRoles.add(ur);
            }
        }
    }

    public abstract static class UserBuilder<C extends User, B extends UserBuilder<C, B>> extends BaseEntityBuilder<C, B> {
        public B password(String password) {
            this.passwordHash = password;
            return self();
        }
    }
}
