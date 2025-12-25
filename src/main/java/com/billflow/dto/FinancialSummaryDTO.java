package com.billflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryDTO {
    private BigDecimal totalRevenue;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private BigDecimal overdueAmount;
}