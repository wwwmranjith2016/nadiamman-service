package com.billflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPerformanceDTO {
    private Long productId;
    private String productName;
    private String category;
    private Integer quantitySold;
    private BigDecimal revenue;
    private Integer remainingStock;
}