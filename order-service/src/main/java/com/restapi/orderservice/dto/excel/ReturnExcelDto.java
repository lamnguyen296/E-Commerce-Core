package com.restapi.orderservice.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@HeadRowHeight(25)
@ContentRowHeight(20)
public class ReturnExcelDto {

    @ExcelProperty(value = "Mã yêu cầu (Return ID)", index = 0)
    @ColumnWidth(18)
    String id;

    @ExcelProperty(value = "Mã đơn hàng (Order Number)", index = 1)
    @ColumnWidth(22)
    String orderNumber;

    @ExcelProperty(value = "Tên khách hàng", index = 2)
    @ColumnWidth(24)
    String customerName;

    @ExcelProperty(value = "Email khách hàng", index = 3)
    @ColumnWidth(28)
    String customerEmail;

    @ExcelProperty(value = "Tên sản phẩm", index = 4)
    @ColumnWidth(35)
    String productName;

    @ExcelProperty(value = "Lý do trả hàng", index = 5)
    @ColumnWidth(22)
    String reason;

    @ExcelProperty(value = "Nguồn yêu cầu (Origin Type)", index = 6)
    @ColumnWidth(24)
    String originType;

    @ExcelProperty(value = "Trạng thái (Status)", index = 7)
    @ColumnWidth(22)
    String status;

    @ExcelProperty(value = "Số tiền hoàn ($)", index = 8)
    @ColumnWidth(18)
    BigDecimal refundAmount;

    @ExcelProperty(value = "Đơn vị vận chuyển", index = 9)
    @ColumnWidth(20)
    String carrier;

    @ExcelProperty(value = "Mã vận đơn (Tracking)", index = 10)
    @ColumnWidth(24)
    String trackingNumber;

    @ExcelProperty(value = "Số lượng", index = 11)
    @ColumnWidth(12)
    Integer itemCount;

    @ExcelProperty(value = "Ngày khởi tạo", index = 12)
    @ColumnWidth(22)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
}
