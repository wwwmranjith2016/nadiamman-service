package com.billflow.service;

import com.billflow.model.Product;
import com.billflow.model.Supplier;
import com.billflow.repository.InvoiceItemRepository;
import com.billflow.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierService supplierService;
    private final InvoiceItemRepository invoiceItemRepository;
    
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            Integer sold = invoiceItemRepository.getTotalSoldQuantity(product.getId());
            product.setSold(sold != null ? sold : 0);
        }
        return products;
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Product getProductBySerialNumber(String serialNumber) {
        return productRepository.findBySerialNumber(serialNumber)
            .orElseThrow(() -> new RuntimeException("Product not found with serial number: " + serialNumber));
    }

    public List<Product> getBatteryProducts() {
        return productRepository.findByIsBattery(true);
    }

    public List<String> getAvailableSerialNumbers(Long productId) {
        Product product = getProductById(productId);
        return product.getSerialNumbers();
    }
    
    @Transactional
    public Product createProduct(Product product) {
        if (product.getSupplier() == null) {
            throw new RuntimeException("Supplier is required for products");
        }
        if (Boolean.TRUE.equals(product.getIsBattery())) {
            product.setStock(product.getSerialNumbers() != null ? product.getSerialNumbers().size() : 0);
        }
        return productRepository.save(product);
    }
    
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        if (productDetails.getSupplier() == null) {
            throw new RuntimeException("Supplier is required for products");
        }

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setSupplier(productDetails.getSupplier());
        product.setSerialNumbers(productDetails.getSerialNumbers());
        product.setIsBattery(productDetails.getIsBattery());
        product.setWarrantyDurationMonths(productDetails.getWarrantyDurationMonths());
        if (Boolean.TRUE.equals(product.getIsBattery())) {
            product.setStock(product.getSerialNumbers() != null ? product.getSerialNumbers().size() : 0);
        } else {
            product.setStock(productDetails.getStock());
        }

        return productRepository.save(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
