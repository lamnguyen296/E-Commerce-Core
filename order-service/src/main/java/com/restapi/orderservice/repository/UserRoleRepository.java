package com.restapi.orderservice.repository;

import com.restapi.orderservice.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    List<UserRole> findByUserId(String userId);
    List<UserRole> findByRoleId(String roleId);
}
