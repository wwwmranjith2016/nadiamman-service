package com.billflow.service;

import com.billflow.dto.InvoiceRequest;
import com.billflow.dto.InvoiceStats;
import com.billflow.model.Client;
import com.billflow.model.Invoice;
import com.billflow.model.InvoiceItem;
import com.billflow.model.Product;
import com.billflow.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final ClientService clientService;
    private final ProductService productService;
    
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAllOrderByDateDesc();
    }
    
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }
    
    public List<Invoice> getInvoicesByStatus(String status) {
        return invoiceRepository.findByStatus(status);
    }
    
    public List<Invoice> getInvoicesByClientId(Long clientId) {
        return invoiceRepository.findByClientId(clientId);
    }
    
    public InvoiceStats getInvoiceStats() {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        
        BigDecimal total = allInvoices.stream()
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal paid = allInvoices.stream()
            .filter(inv -> "paid".equals(inv.getStatus()))
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal pending = allInvoices.stream()
            .filter(inv -> "pending".equals(inv.getStatus()))
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal overdue = allInvoices.stream()
            .filter(inv -> "overdue".equals(inv.getStatus()))
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new InvoiceStats(total, paid, pending, overdue, (long) allInvoices.size());
    }
    
    @Transactional
    public Invoice createInvoice(InvoiceRequest request) {
        // Generate invoice number if not provided
        String invoiceNumber = request.getInvoiceNumber();
        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            invoiceNumber = generateInvoiceNumber();
        }
        
        // Check if invoice number already exists
        if (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            throw new RuntimeException("Invoice number already exists: " + invoiceNumber);
        }
        
        // Get client
        Client client = clientService.getClientById(request.getClientId());
        
        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setClient(client);
        invoice.setDate(request.getDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setTax(request.getTax());
        invoice.setDiscount(request.getDiscount());
        invoice.setStatus(request.getStatus());
        invoice.setNotes(request.getNotes());
        
        // Add items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceRequest.InvoiceItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductById(itemRequest.getProductId());
            
            InvoiceItem item = new InvoiceItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            
            invoice.addItem(item);
            
            BigDecimal itemTotal = itemRequest.getPrice()
                .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            
            //update stock - decrease the stock count
            product.setStock(product.getStock() - item.getQuantity());
            productService.updateProduct(product.getId(),product);
        }
        
        // Calculate totals
        invoice.setSubtotal(subtotal);
        BigDecimal taxAmount = subtotal.multiply(request.getTax()).divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = subtotal.multiply(request.getDiscount()).divide(BigDecimal.valueOf(100));
        BigDecimal total = subtotal.add(taxAmount).subtract(discountAmount);
        invoice.setTotal(total);
        
        return invoiceRepository.save(invoice);
    }
    
    @Transactional
    public Invoice updateInvoice(Long id, InvoiceRequest request) {
        Invoice invoice = getInvoiceById(id);

        // Step 1: Restore stock from existing invoice items before clearing them
        for (InvoiceItem existingItem : invoice.getItems()) {
            Product product = existingItem.getProduct();
            // Restore old quantity back to stock
            product.setStock(product.getStock() + existingItem.getQuantity());
            productService.updateProduct(product.getId(), product);
        }

        // Step 2: Clear existing items
        invoice.getItems().clear();

        // Step 3: Update other invoice details
        if (!invoice.getClient().getId().equals(request.getClientId())) {
            Client client = clientService.getClientById(request.getClientId());
            invoice.setClient(client);
        }

        invoice.setDate(request.getDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setTax(request.getTax());
        invoice.setDiscount(request.getDiscount());
        invoice.setStatus(request.getStatus());
        invoice.setNotes(request.getNotes());

        // Step 4: Add new items and decrease stock again
        BigDecimal subtotal = BigDecimal.ZERO;

        for (InvoiceRequest.InvoiceItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductById(itemRequest.getProductId());

            InvoiceItem item = new InvoiceItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());

            invoice.addItem(item);

            BigDecimal itemTotal = itemRequest.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            // Decrease stock for new quantities
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productService.updateProduct(product.getId(), product);
        }

        // Step 5: Recalculate totals
        invoice.setSubtotal(subtotal);
        BigDecimal taxAmount = subtotal.multiply(request.getTax()).divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = subtotal.multiply(request.getDiscount()).divide(BigDecimal.valueOf(100));
        BigDecimal total = subtotal.add(taxAmount).subtract(discountAmount);
        invoice.setTotal(total);

        return invoiceRepository.save(invoice);
    }

    
    @Transactional
    public Invoice updateInvoiceStatus(Long id, String status) {
        Invoice invoice = getInvoiceById(id);
        invoice.setStatus(status);
        return invoiceRepository.save(invoice);
    }
    
    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = getInvoiceById(id);

        // Step 1: Restore stock for all items in this invoice
        for (InvoiceItem item : invoice.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productService.updateProduct(product.getId(), product);
        }

        // Step 2: Delete invoice
        invoiceRepository.delete(invoice);
    }

    
    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }
}