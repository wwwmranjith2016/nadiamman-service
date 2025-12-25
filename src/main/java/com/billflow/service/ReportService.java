package com.billflow.service;

import com.billflow.dto.*;
import com.billflow.model.*;
import com.billflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    public SalesSummaryReportDTO getSalesSummary(LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoices = invoiceRepository.findAll().stream()
            .filter(inv -> !inv.getDate().isBefore(startDate) && !inv.getDate().isAfter(endDate))
            .collect(Collectors.toList());

        BigDecimal totalRevenue = invoices.stream()
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalInvoices = invoices.size();

        BigDecimal averageOrderValue = totalInvoices > 0 ?
            totalRevenue.divide(BigDecimal.valueOf(totalInvoices), 2, BigDecimal.ROUND_HALF_UP) :
            BigDecimal.ZERO;

        Integer totalProductsSold = invoices.stream()
            .mapToInt(inv -> inv.getItems().stream().mapToInt(InvoiceItem::getQuantity).sum())
            .sum();

        return new SalesSummaryReportDTO(totalRevenue, totalInvoices, averageOrderValue, totalProductsSold);
    }

    public List<ProductPerformanceDTO> getProductPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        return productRepository.findAll().stream()
            .map(product -> {
                Integer sold = invoiceItemRepository.getTotalSoldQuantity(product.getId());
                if (sold == null) sold = 0;

                // Calculate revenue for this product in date range
                BigDecimal revenue = invoiceRepository.findAll().stream()
                    .filter(inv -> !inv.getDate().isBefore(startDate) && !inv.getDate().isAfter(endDate))
                    .flatMap(inv -> inv.getItems().stream())
                    .filter(item -> item.getProduct().getId().equals(product.getId()))
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                return new ProductPerformanceDTO(
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    sold,
                    revenue,
                    product.getStock()
                );
            })
            .collect(Collectors.toList());
    }

    public FinancialSummaryDTO getFinancialSummary(LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoices = invoiceRepository.findAll().stream()
            .filter(inv -> !inv.getDate().isBefore(startDate) && !inv.getDate().isAfter(endDate))
            .collect(Collectors.toList());

        BigDecimal totalRevenue = invoices.stream()
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidAmount = invoices.stream()
            .filter(inv -> "paid".equalsIgnoreCase(inv.getStatus()) || "Paid".equals(inv.getStatus()))
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingAmount = invoices.stream()
            .filter(inv -> "pending".equalsIgnoreCase(inv.getStatus()) || "Pending".equals(inv.getStatus()))
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // For overdue, assuming overdue if pending and date is before today
        BigDecimal overdueAmount = invoices.stream()
            .filter(inv -> ("pending".equalsIgnoreCase(inv.getStatus()) || "Pending".equals(inv.getStatus()))
                && inv.getDate().toLocalDate().isBefore(LocalDate.now()))
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinancialSummaryDTO(totalRevenue, paidAmount, pendingAmount, overdueAmount);
    }

    public List<InventoryStatusDTO> getInventoryStatus() {
        return productRepository.findAll().stream()
            .map(product -> {
                Integer sold = invoiceItemRepository.getTotalSoldQuantity(product.getId());
                if (sold == null) sold = 0;

                BigDecimal totalValue = product.getPrice().multiply(BigDecimal.valueOf(product.getStock()));

                String status;
                if (product.getStock() == 0) {
                    status = "Out of Stock";
                } else if (product.getStock() <= 5) {
                    status = "Low Stock";
                } else {
                    status = "In Stock";
                }

                return new InventoryStatusDTO(
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    product.getStock(),
                    sold,
                    product.getPrice(),
                    totalValue,
                    product.getSupplier() != null ? product.getSupplier().getName() : "N/A",
                    status
                );
            })
            .collect(Collectors.toList());
    }

    public List<BatterySerialReportDTO> getBatterySerialReport(String serialNumber) {
        List<BatterySerialReportDTO> results = new ArrayList<>();

        // First, find serials in current inventory (in stock)
        List<Product> products = productRepository.findAll().stream()
            .filter(product -> product.getSerialNumbers() != null &&
                product.getSerialNumbers().stream()
                    .anyMatch(serial -> serial.toLowerCase().contains(serialNumber.toLowerCase())))
            .collect(Collectors.toList());

        for (Product product : products) {
            List<String> matchingSerials = product.getSerialNumbers().stream()
                .filter(serial -> serial.toLowerCase().contains(serialNumber.toLowerCase()))
                .collect(Collectors.toList());

            for (String actualSerial : matchingSerials) {
                BatterySerialReportDTO dto = new BatterySerialReportDTO();
                dto.setSearchSerialNumber(serialNumber);
                dto.setActualSerialNumber(actualSerial);
                dto.setProductId(product.getId());
                dto.setProductName(product.getName());
                dto.setCategory(product.getCategory());
                dto.setDescription(product.getDescription());
                dto.setPrice(product.getPrice());
                dto.setSupplierName(product.getSupplier() != null ? product.getSupplier().getName() : "N/A");
                dto.setWarrantyMonths(product.getWarrantyDurationMonths());
                dto.setStatus("In Stock");
                dto.setWarrantyStatus("N/A");

                results.add(dto);
            }
        }

        // Then, find serials in sold items
        List<InvoiceItem> soldItems = invoiceItemRepository.findAll().stream()
            .filter(item -> item.getSerialNumbers() != null &&
                item.getSerialNumbers().stream()
                    .anyMatch(serial -> serial.toLowerCase().contains(serialNumber.toLowerCase())))
            .collect(Collectors.toList());

        for (InvoiceItem soldItem : soldItems) {
            List<String> matchingSerials = soldItem.getSerialNumbers().stream()
                .filter(serial -> serial.toLowerCase().contains(serialNumber.toLowerCase()))
                .collect(Collectors.toList());

            for (String actualSerial : matchingSerials) {
                // Check if this serial is already in results (shouldn't be, but to avoid duplicates)
                boolean alreadyExists = results.stream()
                    .anyMatch(dto -> dto.getActualSerialNumber().equals(actualSerial));

                if (!alreadyExists) {
                    Product product = soldItem.getProduct();
                    Invoice invoice = soldItem.getInvoice();

                    BatterySerialReportDTO dto = new BatterySerialReportDTO();
                    dto.setSearchSerialNumber(serialNumber);
                    dto.setActualSerialNumber(actualSerial);
                    dto.setProductId(product.getId());
                    dto.setProductName(product.getName());
                    dto.setCategory(product.getCategory());
                    dto.setDescription(product.getDescription());
                    dto.setPrice(product.getPrice());
                    dto.setSupplierName(product.getSupplier() != null ? product.getSupplier().getName() : "N/A");
                    dto.setWarrantyMonths(product.getWarrantyDurationMonths());
                    dto.setStatus("Sold");
                    dto.setInvoiceNumber(invoice.getInvoiceNumber());
                    dto.setSaleDate(invoice.getDate().toLocalDate());
                    dto.setSalePrice(soldItem.getPrice());

                    Client client = invoice.getClient();
                    dto.setCustomerId(client.getId());
                    dto.setCustomerName(client.getName());
                    dto.setCustomerPhone(client.getPhone());
                    dto.setCustomerEmail(client.getEmail());

                    // Warranty expiry
                    LocalDate expiry = invoice.getDate().toLocalDate().plusMonths(product.getWarrantyDurationMonths());
                    dto.setWarrantyExpiry(expiry);
                    dto.setWarrantyStatus(expiry.isAfter(LocalDate.now()) ? "Active" : "Expired");

                    results.add(dto);
                }
            }
        }

        if (results.isEmpty()) {
            throw new RuntimeException("No products found with serial number containing: " + serialNumber);
        }

        return results;
    }
}