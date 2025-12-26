package com.billflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = true, length = 1000)
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = true)
    private Supplier supplier;

    @ElementCollection
    @CollectionTable(name = "product_serials", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "serial_number")
    @OrderColumn(name = "serial_order")
    private List<String> serialNumbers = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isBattery = false;

    @Column(nullable = false)
    private Integer warrantyDurationMonths = 12; // Default 1 year for batteries

    //    @NotBlank(message = "stock is required")
    @Column(nullable = false)
    private Integer stock;

    @Transient
    private Integer sold;
}
