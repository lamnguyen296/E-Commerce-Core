package com.restapi.orderservice.repository;

import com.restapi.orderservice.entity.TrackingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingLogRepository extends JpaRepository<TrackingLog, String> {
    List<TrackingLog> findByOrderId(String orderId);
    List<TrackingLog> findByShipmentId(String shipmentId);
}
