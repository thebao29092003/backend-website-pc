package com.websitePc.websidePc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.model.Product;
import com.websitePc.websidePc.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductComponentService productComponentService;
    private final ImgService imgService;

    @Transactional
    public void addProductAndComponent(JsonNode productData) {
        // Thêm sản phẩm và lấy ID
        int productInStock = productData.get("productInStock").asInt();
        String productName = productData.get("productName").asText();
        BigDecimal productPrice = new BigDecimal(productData.get("productPrice").asText());
        String productType = productData.get("productType").asText();


        // Tạo thời gian hiện tại
        LocalDate createDate = LocalDateTime.now().toLocalDate();

        productRepository.insertProduct(
                productInStock,
                productName,
                productPrice,
                productType,
                createDate
        );
        Long productId = productRepository.findLastInsertedProduct();

        System.out.println("Product ID: " + productId);
        System.out.println("componentIds: " + productData.get("componentIds"));

        // Thêm các components
        for (JsonNode componentId : productData.get("componentIds")) {
            Long id = componentId.asLong();
            productComponentService.insertProductComponent(id, productId);
        }

        // thêm các imgs
        for (JsonNode imgLinkInput : productData.get("imgLinks")) {
            String imgLink = imgLinkInput.asText();
            imgService.insertImg(imgLink, productId);
        }
    }


    public List<Object[]> productBuyMonths(int month) {
        return productRepository.productBuyMonths(month, 5);
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
