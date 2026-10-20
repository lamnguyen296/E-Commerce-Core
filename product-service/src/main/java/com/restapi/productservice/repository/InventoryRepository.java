package com.restapi.productservice.repository;

import com.restapi.productservice.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    @Query("SELECT i FROM Inventory i WHERE i.productVariant.id = :productVariantId")
    Optional<Inventory> findByProductVariantId(@Param("productVariantId") String productVariantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productVariant.id = :productVariantId")
    Optional<Inventory> findByProductVariantIdWithLock(@Param("productVariantId") String productVariantId);
}
