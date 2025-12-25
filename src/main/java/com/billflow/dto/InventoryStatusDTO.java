package com.billflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatusDTO {
    private Long productId;
    private String productName;
    private String category;
    private Integer currentStock;
    private Integer sold;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private String supplierName;
    private String status; // "In Stock", "Low Stock", "Out of Stock"
}