package com.websitePc.websidePc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;

//    cái controller này để thêm hoặc xóa quyền admin
    @PutMapping("/toggleAdmin")
    @Transactional
    public ResponseEntity<?> toggleAdmin(
            @RequestParam("userId") String userId,
            @RequestParam("role") String role
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            productService.toggleAdmin(userId, role);
            response.put("status", "success");
            response.put("message", "Change role successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error change role: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/productBuyMonths/{month}")
    public Map<String, Object> productBuyMonths(@PathVariable int month) {
        List<Object[]> productPage = productService.productBuyMonths(month);
        Map<String, Object> response = new HashMap<>();
        response.put("productList", productPage);
        return response;
    }

    @GetMapping("/product/{pageNo}")
    public ResponseEntity<?> pcListNew(@PathVariable int pageNo) {
        int size = 10;

        Page<Object[]> productPage = productService.listProductForAdmin(pageNo, size);
        Map<String, Object> response = new HashMap<>();
        response.put("productList", productPage.getContent());
        response.put("currentPage", pageNo);
        response.put("totalPages", productPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product/{productId}")
    @Transactional
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete product: " + e.getMessage()));
        }
    }
}
