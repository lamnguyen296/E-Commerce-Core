package com.restapi.orderservice.repository;

import com.restapi.orderservice.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, String> {
    List<ShippingRate> findByCarrierId(String carrierId);
}
