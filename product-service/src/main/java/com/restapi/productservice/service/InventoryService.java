package com.restapi.productservice.service;

import com.restapi.productservice.dto.request.StockReservationRequest;

import java.util.List;

public interface InventoryService {
    boolean checkStock(String variantId, int quantity);
    void reserveStock(List<StockReservationRequest> requests);
    void releaseStock(List<StockReservationRequest> requests);
}
