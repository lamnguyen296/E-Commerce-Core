package com.restapi.orderservice.service;

import com.restapi.orderservice.dto.excel.ReturnImportResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface ReturnService {
    /**
     * Xuất dữ liệu lớn ra file Excel (.xlsx) chuẩn Production bằng Alibaba EasyExcel.
     * Sử dụng SAX stream, phân trang 500 bản ghi/lần, không gây OOM ngay cả với hàng triệu dòng.
     */
    void exportReturnsToExcelStream(String status, String search, String currentUser, OutputStream outputStream) throws IOException;

    /**
     * Nhập dữ liệu lớn từ file Excel (.xlsx / .xls) chuẩn Production bằng Alibaba EasyExcel.
     * Phân tích luồng theo sự kiện SAX, lưu theo lô (batch insert 500 dòng), bộ nhớ RAM cố định.
     */
    ReturnImportResult importReturnsFromExcel(MultipartFile file, String currentUser) throws IOException;

    /**
     * Xuất dữ liệu trả hàng dạng stream CSV
     */
    void exportReturnsToCsvStream(String status, String search, String currentUser, OutputStream outputStream) throws IOException;

    /**
     * Phương thức dự phòng xuất CSV dạng byte[]
     */
    byte[] exportReturnsToCsv(String status, String search);
}
