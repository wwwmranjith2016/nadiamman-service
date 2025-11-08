package com.billflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceStats {
    private BigDecimal total;
    private BigDecimal paid;
    private BigDecimal pending;
    private BigDecimal overdue;
    private Long count;
}
