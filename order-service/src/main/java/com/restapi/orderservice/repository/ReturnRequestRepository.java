package com.restapi.orderservice.repository;

import com.restapi.orderservice.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, String>, JpaSpecificationExecutor<ReturnRequest> {
    List<ReturnRequest> findByStatusIgnoreCase(String status);
    List<ReturnRequest> findAllByOrderByCreatedAtDesc();
}
