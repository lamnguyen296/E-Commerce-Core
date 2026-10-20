package com.restapi.orderservice.repository;

import com.restapi.orderservice.dto.excel.ReturnImportResult;
import com.restapi.orderservice.entity.ReturnRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnBatchRepository {

    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReturnBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }


    private static final String UPSERT_SQL = """
            INSERT INTO return_requests (
                id, order_number, customer_name, customer_email, product_name,
                reason, origin_type, status, refund_amount, carrier,
                tracking_number, item_count, created_at, created_by, updated_at,
                updated_by, deleted
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                order_number = VALUES(order_number),
                customer_name = VALUES(customer_name),
                customer_email = VALUES(customer_email),
                product_name = VALUES(product_name),
                reason = VALUES(reason),
                origin_type = VALUES(origin_type),
                status = VALUES(status),
                refund_amount = VALUES(refund_amount),
                carrier = VALUES(carrier),
                tracking_number = VALUES(tracking_number),
                item_count = VALUES(item_count),
                updated_at = VALUES(updated_at),
                updated_by = VALUES(updated_by);
            """;

    /**
     * Thực thi Batch UPSERT tốc độ cao bằng MySQL ON DUPLICATE KEY UPDATE:
     * 1. Pre-check các ID đã tồn tại trong DB trong batch 500 ID qua primary key index (chỉ mất ~1ms).
     * 2. Thực thi batchUpdate đa dòng cực nhanh.
     * 3. Thống kê chính xác số lượng Tạo mới (inserted) và Ghi đè (updated).
     */
    public void batchUpsert(List<ReturnRequest> list, ReturnImportResult result) {
        if (list == null || list.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. Tìm các ID đã tồn tại trong DB (1 query cho cả 500 ID)
        List<String> ids = list.stream()
                .map(ReturnRequest::getId)
                .filter(Objects::nonNull)
                .toList();

        Set<String> existingIdsInDb = new HashSet<>();
        if (!ids.isEmpty()) {
            MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
            existingIdsInDb.addAll(
                    namedParameterJdbcTemplate.query(
                            "SELECT id FROM return_requests WHERE id IN (:ids)",
                            params,
                            (rs, rowNum) -> rs.getString("id")
                    )
            );
        }

        // 2. Thực thi Batch UPSERT
        jdbcTemplate.batchUpdate(
                UPSERT_SQL,
                list,
                list.size(),
                (PreparedStatement ps, ReturnRequest r) -> {
                    ps.setString(1, r.getId());
                    ps.setString(2, r.getOrderNumber());
                    ps.setString(3, r.getCustomerName());
                    ps.setString(4, r.getCustomerEmail());
                    ps.setString(5, r.getProductName());
                    ps.setString(6, r.getReason());
                    ps.setString(7, r.getOriginType());
                    ps.setString(8, r.getStatus());
                    ps.setBigDecimal(9, r.getRefundAmount());
                    ps.setString(10, r.getCarrier());
                    ps.setString(11, r.getTrackingNumber());
                    ps.setInt(12, r.getItemCount() != null ? r.getItemCount() : 1);
                    ps.setTimestamp(13, r.getCreatedAt() != null ? Timestamp.valueOf(r.getCreatedAt()) : Timestamp.valueOf(now));
                    ps.setString(14, r.getCreatedBy());
                    ps.setTimestamp(15, Timestamp.valueOf(now));
                    ps.setString(16, r.getUpdatedBy());
                    ps.setBoolean(17, r.getDeleted() != null ? r.getDeleted() : false);
                }
        );

        // 3. Phân loại chính xác số lượng tạo mới và ghi đè
        int inserted = 0;
        int updated = 0;
        Set<String> seenInBatch = new HashSet<>();

        for (ReturnRequest r : list) {
            String id = r.getId();
            if (existingIdsInDb.contains(id) || seenInBatch.contains(id)) {
                updated++;
            } else {
                inserted++;
                seenInBatch.add(id);
            }
        }

        result.setInsertedCount(result.getInsertedCount() + inserted);
        result.setUpdatedCount(result.getUpdatedCount() + updated);

        log.info("Batch UPSERT hoàn tất {} bản ghi (Tạo mới: {}, Ghi đè: {})", list.size(), inserted, updated);
    }
}
