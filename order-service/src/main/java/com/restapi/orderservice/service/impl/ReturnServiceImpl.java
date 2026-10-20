package com.restapi.orderservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.restapi.orderservice.dto.excel.ReturnExcelDto;
import com.restapi.orderservice.dto.excel.ReturnImportResult;
import com.restapi.orderservice.entity.ReturnRequest;
import com.restapi.orderservice.repository.ReturnBatchRepository;
import com.restapi.orderservice.repository.ReturnRequestRepository;
import com.restapi.orderservice.repository.specification.ReturnRequestSpecification;
import com.restapi.orderservice.service.ReturnService;
import com.restapi.orderservice.service.listener.ReturnExcelImportListener;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnServiceImpl implements ReturnService {

    ReturnRequestRepository returnRequestRepository;
    ReturnBatchRepository returnBatchRepository;

    private static final int BATCH_SIZE = 500;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public void exportReturnsToExcelStream(String status, String search, String currentUser, OutputStream outputStream) throws IOException {
        Specification<ReturnRequest> spec = ReturnRequestSpecification.getFilterSpec(status, search);

        // Sử dụng Alibaba EasyExcel ghi trực tiếp ra OutputStream theo phân trang
        // EasyExcel tự động dùng bộ nhớ đệm đĩa khi dữ liệu lớn, đảm bảo RAM không bị tràn (Anti-OOM)
        try (ExcelWriter excelWriter = EasyExcel.write(outputStream, ReturnExcelDto.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Danh Sách Trả Hàng").build();

            int pageNumber = 0;
            long totalExported = 0;
            Page<ReturnRequest> batch;

            do {
                Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
                batch = returnRequestRepository.findAll(spec, pageable);

                List<ReturnExcelDto> dtoList = batch.getContent().stream()
                        .map(this::mapToExcelDto)
                        .toList();

                excelWriter.write(dtoList, writeSheet);
                totalExported += batch.getNumberOfElements();
                pageNumber++;
            } while (batch.hasNext());

            log.info("[AUDIT-EXCEL] User '{}' đã xuất thành công {} bản ghi ra Excel bằng EasyExcel (status='{}', search='{}')",
                    currentUser != null ? currentUser : "system", totalExported, status, search);
        }
    }

    @Override
    @Transactional
    public ReturnImportResult importReturnsFromExcel(MultipartFile file, String currentUser) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File Excel tải lên không được để trống!");
        }

        log.info("[AUDIT-IMPORT] Bắt đầu đọc file Excel '{}' (kích thước {} bytes) bởi user '{}'",
                file.getOriginalFilename(), file.getSize(), currentUser);

        ReturnExcelImportListener listener = new ReturnExcelImportListener(returnBatchRepository, currentUser);

        // EasyExcel đọc luồng SAX sự kiện, không load toàn bộ DOM vào RAM
        EasyExcel.read(file.getInputStream(), ReturnExcelDto.class, listener)
                .sheet()
                .doRead();

        return listener.getResult();
    }

    @Override
    @Transactional(readOnly = true)
    public void exportReturnsToCsvStream(String status, String search, String currentUser, OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        // 1. Ghi UTF-8 BOM để Excel tự nhận diện đúng bảng mã UTF-8
        writer.write('\uFEFF');

        // 2. Ghi dòng tiêu đề cột
        writer.write("Return ID,Customer Name,Customer Email,Order Number,Product,Reason,Origin Type,Status,Refund Amount,Carrier,Tracking Number,Created Date\n");
        writer.flush();

        // 3. Sử dụng JPA Specification để lọc dữ liệu trực tiếp tại Database
        Specification<ReturnRequest> spec = ReturnRequestSpecification.getFilterSpec(status, search);

        int pageNumber = 0;
        long totalExported = 0;
        Page<ReturnRequest> batch;

        do {
            Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
            batch = returnRequestRepository.findAll(spec, pageable);

            for (ReturnRequest r : batch.getContent()) {
                String formattedDate = r.getCreatedAt() != null ? r.getCreatedAt().format(DATE_FORMATTER) : "";

                writer.write(sanitizeAndEscapeCsv(r.getId()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getCustomerName()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getCustomerEmail()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getOrderNumber()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getProductName()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getReason()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getOriginType()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getStatus()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getRefundAmount() != null ? "$" + r.getRefundAmount().toPlainString() : "$0.00"));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getCarrier()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(r.getTrackingNumber()));
                writer.write(",");
                writer.write(sanitizeAndEscapeCsv(formattedDate));
                writer.write("\n");
            }

            // Flush theo từng batch 500 bản ghi ra mạng, giải phóng RAM
            writer.flush();
            totalExported += batch.getNumberOfElements();
            pageNumber++;
        } while (batch.hasNext());

        log.info("[AUDIT] User '{}' đã xuất thành công {} bản ghi yêu cầu trả hàng ra CSV (status='{}', search='{}')",
                currentUser != null ? currentUser : "system", totalExported, status, search);
    }

    @Override
    public byte[] exportReturnsToCsv(String status, String search) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            exportReturnsToCsvStream(status, search, "internal-call", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Lỗi khi tạo dữ liệu CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể xuất file CSV: " + e.getMessage(), e);
        }
    }

    private ReturnExcelDto mapToExcelDto(ReturnRequest r) {
        return ReturnExcelDto.builder()
                .id(sanitizeText(r.getId()))
                .orderNumber(sanitizeText(r.getOrderNumber()))
                .customerName(sanitizeText(r.getCustomerName()))
                .customerEmail(sanitizeText(r.getCustomerEmail()))
                .productName(sanitizeText(r.getProductName()))
                .reason(sanitizeText(r.getReason()))
                .originType(sanitizeText(r.getOriginType()))
                .status(sanitizeText(r.getStatus()))
                .refundAmount(r.getRefundAmount() != null ? r.getRefundAmount() : BigDecimal.ZERO)
                .carrier(sanitizeText(r.getCarrier()))
                .trackingNumber(sanitizeText(r.getTrackingNumber()))
                .itemCount(r.getItemCount() != null ? r.getItemCount() : 1)
                .createdAt(r.getCreatedAt())
                .build();
    }

    private String sanitizeText(String value) {
        if (value == null) return "";
        String val = value.trim();
        // Chống Formula Injection
        if (val.startsWith("=") || val.startsWith("+") || val.startsWith("-") || val.startsWith("@") || val.startsWith("\t")) {
            return "'" + val;
        }
        return val;
    }

    private String sanitizeAndEscapeCsv(String value) {
        if (value == null) {
            return "\"\"";
        }
        String val = sanitizeText(value);
        String escaped = val.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
