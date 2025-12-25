package com.billflow.repository;

import com.billflow.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByProductId(Long productId);

    @Query("SELECT SUM(ii.quantity) FROM InvoiceItem ii WHERE ii.product.id = :productId")
    Integer getTotalSoldQuantity(@Param("productId") Long productId);
}