package com.restapi.orderservice.repository;

import com.restapi.orderservice.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {

    Optional<Discount> findByCode(String code);

    Optional<Discount> findByCodeAndIsActiveTrue(String code);

    boolean existsByCode(String code);

    @Query("SELECT d FROM Discount d WHERE d.deleted = false AND d.isActive = true " +
           "AND (d.startDate IS NULL OR d.startDate <= :now) " +
           "AND (d.endDate IS NULL OR d.endDate >= :now) " +
           "AND (d.usageLimit IS NULL OR d.usageCount < d.usageLimit) " +
           "AND (d.user IS NULL OR d.user.id = :userId)")
    List<Discount> findAvailableDiscountsForUser(@Param("userId") String userId, @Param("now") LocalDateTime now);
}
