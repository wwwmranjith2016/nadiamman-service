package com.billflow.repository;

import com.billflow.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByClientId(Long clientId);
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    @Query("SELECT i FROM Invoice i ORDER BY i.date DESC")
    List<Invoice> findAllOrderByDateDesc();
}