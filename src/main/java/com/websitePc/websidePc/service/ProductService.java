package com.websitePc.websidePc.service;

import com.websitePc.websidePc.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Object[]> productBuyMonths(int month) {
        return productRepository.productBuyMonths(month);
    }

    public void deleteProductById(Long productId) {
        productRepository.deleteProductById(productId);
    }

    public Page<Object[]> listProductForAdmin(int page, int size) {
        return productRepository.listProductForAdmin(PageRequest.of(page, size));
    }

    public List<Object[]> recommendByPrice(Long productId, BigDecimal price, BigDecimal priceRange, String type) {
        return productRepository.recommendByPrice(productId, price, priceRange, type);
    }

    public Object getProductById(Long id) {
        return productRepository.findProductById(id);
    }

    public Page<Object[]> listPcNew(int page, int size) {
        return productRepository.listProductNew(PageRequest.of(page, size), "PC");
    }

    public Page<Object[]> listLaptopNew(int page, int size) {
        return productRepository.listProductNew(PageRequest.of(page, size), "Laptop");
    }

    public Page<Object[]> categoryCpuVga(int page, int size, String componentType, String componentName) {
        return productRepository.categoryCpuVga(PageRequest.of(page, size), componentType, componentName);
    }

    public Page<Object[]> findProductByName(int page, int size, String productName) {
        return productRepository.findProductByName(PageRequest.of(page, size), productName);
    }
    public Page<Object[]> findProductByOrderId(String orderId, int page, int size) {
        return productRepository.findProductByOrderId(orderId, PageRequest.of(page, size));
    }
}
