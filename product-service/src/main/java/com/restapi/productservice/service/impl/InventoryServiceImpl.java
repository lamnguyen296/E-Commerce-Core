package com.restapi.productservice.service.impl;

import com.restapi.productservice.dto.request.StockReservationRequest;
import com.restapi.productservice.entity.Inventory;
import com.restapi.productservice.exception.AppException;
import com.restapi.productservice.exception.ErrorCode;
import com.restapi.productservice.repository.InventoryRepository;
import com.restapi.productservice.service.InventoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryServiceImpl implements InventoryService {

    InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean checkStock(String variantId, int quantity) {
        return inventoryRepository.findByProductVariantId(variantId)
                .map(inv -> inv.getQuantityInStock() >= quantity)
                .orElse(false);
    }

    @Override
    @Transactional
    public void reserveStock(List<StockReservationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        // Sắp xếp theo variantId để tránh deadlock khi có nhiều request đồng thời
        List<StockReservationRequest> sorted = requests.stream()
                .sorted(Comparator.comparing(StockReservationRequest::getVariantId))
                .toList();

        for (StockReservationRequest req : sorted) {
            Inventory inv = inventoryRepository.findByProductVariantIdWithLock(req.getVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.OUT_OF_STOCK));

            if (inv.getQuantityInStock() < req.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            inv.setQuantityInStock(inv.getQuantityInStock() - req.getQuantity());
            inventoryRepository.save(inv);
        }
    }

    @Override
    @Transactional
    public void releaseStock(List<StockReservationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        for (StockReservationRequest req : requests) {
            inventoryRepository.findByProductVariantIdWithLock(req.getVariantId())
                    .ifPresent(inv -> {
                        inv.setQuantityInStock(inv.getQuantityInStock() + req.getQuantity());
                        inventoryRepository.save(inv);
                    });
        }
    }
}
