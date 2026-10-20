package com.restapi.orderservice.controller;

import com.restapi.orderservice.dto.excel.ReturnImportResult;
import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.service.ReturnService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/returns")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnController {

    ReturnService returnService;

    /**
     * API 1: Xuất file Excel (.xlsx) chuẩn Production bằng Alibaba EasyExcel
     * - Sử dụng bộ nhớ đệm đĩa và luồng SAX, không tích lũy RAM
     * - Tự động định dạng độ rộng cột, font chữ, ngày giờ
     * - Khả năng chịu tải hàng triệu dòng dữ liệu
     */
    @GetMapping("/export/excel")
    public ResponseEntity<StreamingResponseBody> exportReturnsToExcel(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : "admin";

        StreamingResponseBody stream = outputStream -> {
            try {
                returnService.exportReturnsToExcelStream(status, search, currentUser, outputStream);
            } catch (Exception e) {
                log.error("Lỗi khi stream dữ liệu Excel qua EasyExcel cho user {}: {}", currentUser, e.getMessage(), e);
            }
        };

        String filename = "returns_export_" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(stream);
    }

    /**
     * API 2: Xuất file CSV dạng Streaming
     */
    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportReturnsToCsv(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : "admin";

        StreamingResponseBody stream = outputStream -> {
            try {
                returnService.exportReturnsToCsvStream(status, search, currentUser, outputStream);
            } catch (Exception e) {
                log.error("Lỗi trong quá trình stream dữ liệu CSV cho user {}: {}", currentUser, e.getMessage(), e);
            }
        };

        String filename = "returns_export_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(stream);
    }

    /**
     * API 3: Nhập dữ liệu lớn (Batch Import) từ file Excel bằng Alibaba EasyExcel
     * - Xử lý theo sự kiện SAX từng dòng (Row by Row)
     * - Lưu vào DB theo lô (batch 500 dòng), bộ nhớ RAM duy trì < 5MB
     * - Báo cáo chi tiết số lượng thành công, thất bại và chi tiết lỗi
     */
    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<ReturnImportResult> importReturnsFromExcel(
            @RequestParam("file") MultipartFile file) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : "admin";

        ReturnImportResult result = returnService.importReturnsFromExcel(file, currentUser);
        return BaseResponse.ofSuccess(result);
    }
}
