package com.restapi.orderservice.dto.excel;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnImportResult {
    int totalRows;
    int successCount;
    int insertedCount; // Số dòng tạo mới thành công
    int updatedCount;  // Số dòng trùng lặp đã được ghi đè (UPSERT)
    int failedCount;   // Số dòng bị lỗi
    @Builder.Default
    List<String> errorMessages = new ArrayList<>();
}
