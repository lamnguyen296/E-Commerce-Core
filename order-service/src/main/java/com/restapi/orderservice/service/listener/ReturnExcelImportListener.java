package com.restapi.orderservice.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.restapi.orderservice.dto.excel.ReturnExcelDto;
import com.restapi.orderservice.dto.excel.ReturnImportResult;
import com.restapi.orderservice.entity.ReturnRequest;
import com.restapi.orderservice.repository.ReturnBatchRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReturnExcelImportListener implements ReadListener<ReturnExcelDto> {

    private final ReturnBatchRepository returnBatchRepository;
    private final String currentUser;

    private static final int BATCH_SIZE = 500;
    private final List<ReturnRequest> cachedDataList = new ArrayList<>(BATCH_SIZE);

    @Getter
    private final ReturnImportResult result = ReturnImportResult.builder().build();

    public ReturnExcelImportListener(ReturnBatchRepository returnBatchRepository, String currentUser) {
        this.returnBatchRepository = returnBatchRepository;
        this.currentUser = currentUser;
    }

    @Override
    public void invoke(ReturnExcelDto data, AnalysisContext context) {
        result.setTotalRows(result.getTotalRows() + 1);

        try {
            // Validate dữ liệu bắt buộc
            if (data.getCustomerName() == null || data.getCustomerName().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên khách hàng không được để trống");
            }
            if (data.getOrderNumber() == null || data.getOrderNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Mã đơn hàng không được để trống");
            }

            // Nếu ID trong Excel chưa có, sinh ID tự động. Nếu đã có thì giữ nguyên để ghi đè (UPSERT)
            String returnId = (data.getId() != null && !data.getId().trim().isEmpty())
                    ? data.getId().trim()
                    : "#RET-" + (8800 + (int)(Math.random() * 9000));

            LocalDateTime now = LocalDateTime.now();

            ReturnRequest entity = ReturnRequest.builder()
                    .id(returnId)
                    .orderNumber(data.getOrderNumber().trim())
                    .customerName(data.getCustomerName().trim())
                    .customerEmail(data.getCustomerEmail())
                    .productName(data.getProductName())
                    .reason(data.getReason() != null ? data.getReason() : "Customer Return")
                    .originType(data.getOriginType() != null ? data.getOriginType() : "CUSTOMER REQUESTED")
                    .status(data.getStatus() != null ? data.getStatus() : "PENDING ACTION")
                    .refundAmount(data.getRefundAmount() != null ? data.getRefundAmount() : BigDecimal.ZERO)
                    .carrier(data.getCarrier())
                    .trackingNumber(data.getTrackingNumber())
                    .itemCount(data.getItemCount() != null ? data.getItemCount() : 1)
                    .createdAt(data.getCreatedAt() != null ? data.getCreatedAt() : now)
                    .createdBy(currentUser)
                    .updatedAt(now)
                    .updatedBy(currentUser)
                    .deleted(false)
                    .build();

            cachedDataList.add(entity);

            // Khi bộ đệm đạt BATCH_SIZE (500 dòng), thực thi Batch UPSERT và giải phóng RAM
            if (cachedDataList.size() >= BATCH_SIZE) {
                saveBatchData();
            }
        } catch (Exception e) {
            result.setFailedCount(result.getFailedCount() + 1);
            int rowIndex = context.readRowHolder().getRowIndex() + 1;
            String errMsg = String.format("Dòng %d: %s", rowIndex, e.getMessage());
            log.warn("Lỗi đọc dòng Excel: {}", errMsg);
            result.getErrorMessages().add(errMsg);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // Lưu nốt số lượng còn lại trong bộ đệm
        if (!cachedDataList.isEmpty()) {
            saveBatchData();
        }

        // Tổng số thành công = Thêm mới + Ghi đè cập nhật
        result.setSuccessCount(result.getInsertedCount() + result.getUpdatedCount());

        log.info("[AUDIT-IMPORT] Hoàn tất import Excel bởi '{}': Tổng {} dòng, Tạo mới: {}, Ghi đè: {}, Lỗi: {}",
                currentUser, result.getTotalRows(), result.getInsertedCount(), result.getUpdatedCount(), result.getFailedCount());
    }

    private void saveBatchData() {
        log.debug("Lưu batch {} bản ghi vào database bằng MySQL Native Batch UPSERT...", cachedDataList.size());
        returnBatchRepository.batchUpsert(cachedDataList, result);
        cachedDataList.clear(); // Giải phóng bộ nhớ RAM ngay lập tức
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("Lỗi khi parse file Excel: {}", exception.getMessage());
        if (exception instanceof IllegalArgumentException) {
            result.setFailedCount(result.getFailedCount() + 1);
            int rowIndex = context.readRowHolder().getRowIndex() + 1;
            result.getErrorMessages().add(String.format("Dòng %d: %s", rowIndex, exception.getMessage()));
        } else {
            throw exception;
        }
    }
}
