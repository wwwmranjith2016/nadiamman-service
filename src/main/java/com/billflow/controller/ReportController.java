package com.billflow.controller;

import com.billflow.dto.*;
import com.billflow.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales-summary")
    public ResponseEntity<SalesSummaryReportDTO> getSalesSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getSalesSummary(startDate, endDate));
    }

    @GetMapping("/product-performance")
    public ResponseEntity<List<ProductPerformanceDTO>> getProductPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getProductPerformance(startDate, endDate));
    }

    @GetMapping("/financial-summary")
    public ResponseEntity<FinancialSummaryDTO> getFinancialSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getFinancialSummary(startDate, endDate));
    }

    @GetMapping("/inventory-status")
    public ResponseEntity<List<InventoryStatusDTO>> getInventoryStatus() {
        return ResponseEntity.ok(reportService.getInventoryStatus());
    }

    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<List<BatterySerialReportDTO>> getBatterySerialReport(@PathVariable String serialNumber) {
        return ResponseEntity.ok(reportService.getBatterySerialReport(serialNumber));
    }
}