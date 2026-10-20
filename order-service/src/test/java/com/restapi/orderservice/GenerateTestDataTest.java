package com.restapi.orderservice;

import com.alibaba.excel.EasyExcel;
import com.restapi.orderservice.dto.excel.ReturnExcelDto;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateTestDataTest {

    private static final String[] CUSTOMER_NAMES = {
            "Eleanor Shellstrop", "Chidi Anagonye", "Tahani Al-Jamil", "Jason Mendoza",
            "Michael Realman", "Janet Subroutine", "Simone Garnet", "Doug Forcett",
            "Nguyen Van An", "Tran Thi Bich", "Le Hoang Nam", "Pham Thu Huong",
            "Vu Duc Minh", "Hoang Thi Mai", "Do Tuan Kiet", "Bui Thanh Hang"
    };

    private static final String[] PRODUCTS = {
            "Ergonomic Executive Chair (Midnight Navy)",
            "Philosophy & Ethics Hardcover Book Set",
            "Luxury Cashmere Throw & Silk Cushion Set",
            "Portable Bluetooth Party Speaker 500W",
            "Architectural Drafting Set Deluxe",
            "Multi-Sensor Smart Home Hub v3",
            "Wireless Ergonomic Trackball Mouse",
            "Eco-Friendly Hemp Backpack & Flask",
            "Mechanical Keyboard RGB Gateron Pro",
            "Smart Noise-Cancelling Headphones Pro"
    };

    private static final String[] REASONS = {
            "Damaged on arrival", "Failed Delivery", "Wrong Item Sent",
            "Defective Unit", "Customer Exchange", "Late Arrival", "Unwanted Gift"
    };

    private static final String[] STATUSES = {
            "PENDING ACTION", "IN-TRANSIT", "WAREHOUSE RECEIVED", "RESTOCKED", "REFUNDED"
    };

    private static final String[] ORIGINS = {
            "CUSTOMER REQUESTED", "FAILED DELIVERY"
    };

    private static final String[] CARRIERS = {
            "FedEx Priority", "DHL Express", "UPS Worldwide", "USPS Priority", "VNPost Express", "GHTK"
    };

    @Test
    void generate10000RowsFiles() throws Exception {
        int totalRows = 10000;
        List<ReturnExcelDto> list = new ArrayList<>(totalRows);
        Random random = new Random(42); // Seed cố định để dữ liệu nhất quán

        System.out.println("Đang tạo 10,000 dòng dữ liệu test...");

        // 1. Tạo 10 dòng đầu tiên trùng với các ID có sẵn trong DB để test GHI ĐÈ (UPSERT)
        String[] existingIds = {"#RET-8842", "#RET-8841", "#RET-8839", "#RET-8835", "#RET-8830", "#RET-8828"};
        for (int i = 0; i < existingIds.length; i++) {
            list.add(ReturnExcelDto.builder()
                    .id(existingIds[i])
                    .orderNumber("Order #ORD-" + (9000 + i) + "-X")
                    .customerName(CUSTOMER_NAMES[i % CUSTOMER_NAMES.length])
                    .customerEmail("customer" + (i + 1) + "@example.com")
                    .productName(PRODUCTS[i % PRODUCTS.length])
                    .reason(REASONS[i % REASONS.length])
                    .originType(ORIGINS[i % ORIGINS.length])
                    .status("REFUNDED") // Cập nhật sang REFUNDED để thấy rõ việc ghi đè
                    .refundAmount(BigDecimal.valueOf(100.0 + i * 50.0).setScale(2, RoundingMode.HALF_UP))
                    .carrier(CARRIERS[i % CARRIERS.length])
                    .trackingNumber("TRK-" + (10000 + i))
                    .itemCount(random.nextInt(3) + 1)
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                    .build());
        }

        // 2. Tạo 9,994 dòng mới tinh để test TẠO MỚI (INSERT)
        for (int i = existingIds.length; i < totalRows; i++) {
            int num = 10000 + i;
            list.add(ReturnExcelDto.builder()
                    .id("#RET-" + num)
                    .orderNumber("Order #ORD-" + num + "-" + (char)('A' + (i % 26)))
                    .customerName(CUSTOMER_NAMES[random.nextInt(CUSTOMER_NAMES.length)])
                    .customerEmail("user" + num + "@example.com")
                    .productName(PRODUCTS[random.nextInt(PRODUCTS.length)])
                    .reason(REASONS[random.nextInt(REASONS.length)])
                    .originType(ORIGINS[random.nextInt(ORIGINS.length)])
                    .status(STATUSES[random.nextInt(STATUSES.length)])
                    .refundAmount(BigDecimal.valueOf(20.0 + random.nextDouble() * 980.0).setScale(2, RoundingMode.HALF_UP))
                    .carrier(CARRIERS[random.nextInt(CARRIERS.length)])
                    .trackingNumber("TRK-" + (random.nextInt(899999) + 100000))
                    .itemCount(random.nextInt(4) + 1)
                    .createdAt(LocalDateTime.now().minusHours(random.nextInt(500)))
                    .build());
        }

        // Ghi file Excel .xlsx bằng EasyExcel
        String excelPath = "d:/Project/test_returns_10000.xlsx";
        EasyExcel.write(excelPath, ReturnExcelDto.class)
                .sheet("Returns Data")
                .doWrite(list);
        System.out.println("-> Đã tạo thành công file Excel: " + excelPath);

        // Ghi thêm file CSV .csv tương ứng
        String csvPath = "d:/Project/test_returns_10000.csv";
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvPath), StandardCharsets.UTF_8))) {
            writer.write('\uFEFF'); // UTF-8 BOM
            writer.write("Mã yêu cầu (Return ID),Mã đơn hàng (Order Number),Tên khách hàng,Email khách hàng,Tên sản phẩm,Lý do trả hàng,Nguồn yêu cầu (Origin Type),Trạng thái (Status),Số tiền hoàn ($),Đơn vị vận chuyển,Mã vận đơn (Tracking),Số lượng,Ngày khởi tạo\n");
            for (ReturnExcelDto r : list) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s,\"%s\",\"%s\",%d,\"%s\"\n",
                        r.getId(), r.getOrderNumber(), r.getCustomerName(), r.getCustomerEmail(),
                        r.getProductName(), r.getReason(), r.getOriginType(), r.getStatus(),
                        r.getRefundAmount(), r.getCarrier(), r.getTrackingNumber(), r.getItemCount(), r.getCreatedAt()));
            }
        }
        System.out.println("-> Đã tạo thành công file CSV: " + csvPath);
    }

    @Test
    void resetReturnRequestsTable() {
        String url = "jdbc:mysql://localhost:3306/ecommerce";
        String user = "root";
        String pass = "root";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass);
             java.sql.Statement stmt = conn.createStatement()) {
            int rowsDeleted = stmt.executeUpdate("DELETE FROM return_requests");
            System.out.println("==================================================");
            System.out.println("-> ĐÃ XÓA SẠCH AN TOÀN " + rowsDeleted + " BẢN GHI TRONG BẢNG return_requests!");
            System.out.println("==================================================");
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa bảng return_requests: " + e.getMessage());
        }
    }
}
