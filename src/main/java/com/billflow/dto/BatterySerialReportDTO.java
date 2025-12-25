package com.billflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatterySerialReportDTO {
    private String searchSerialNumber; // The searched term
    private String actualSerialNumber; // The exact serial found
    private String status; // "Sold", "In Stock"

    // Product Details
    private Long productId;
    private String productName;
    private String category;
    private String description;
    private BigDecimal price;
    private String supplierName;

    // Customer Details (if sold)
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Sale Details (if sold)
    private String invoiceNumber;
    private LocalDate saleDate;
    private BigDecimal salePrice;

    // Warranty Details
    private Integer warrantyMonths;
    private LocalDate warrantyExpiry;
    private String warrantyStatus; // "Active", "Expired", "N/A"
}