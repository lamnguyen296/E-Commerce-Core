package com.restapi.orderservice.repository;

import com.restapi.orderservice.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, String> {
    Optional<Carrier> findByCode(String code);
}
