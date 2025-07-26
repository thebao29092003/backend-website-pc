package com.websitePc.websidePc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.service.ComponentService;
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
    private final ComponentService componentService;

    @PostMapping("/addComponent")
    public ResponseEntity<?> addComponent(@RequestBody JsonNode componentData) {
        Map<String, Object> response = new HashMap<>();
        try{
            // Gọi service để xử lý logic thêm game từ dữ liệu IGDB.
            componentService.addComponent(componentData);
            response.put("status", "success");
            response.put("message", "Component added successfully");
            // Trả về HTTP 200 OK kèm message thành công.
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("status", "error");
            response.put("message", "Error adding Component: " + e.getMessage());
//            Trả về HTTP 500 Internal Server Error.Thêm thông báo lỗi từ exception (e.getMessage()).
            return ResponseEntity.internalServerError().body(response);
        }
    }

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

    @GetMapping("/component/{pageNo}")
    public ResponseEntity<?> componentList(@PathVariable int pageNo) {
        int size = 10;

        Page<Object[]> componentPage = componentService.listComponentForAdmin(pageNo, size);
        Map<String, Object> response = new HashMap<>();
        response.put("componentList", componentPage.getContent());
        response.put("currentPage", pageNo);
        response.put("totalPages", componentPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/componentByName")
    public ResponseEntity<?> componentListByName(@RequestParam ("page") int pageNo,
                                                 @RequestParam ("name") String name) {
        int size = 10;

        Page<Object[]> componentPage = componentService.listComponentByName(pageNo, size, name);
        Map<String, Object> response = new HashMap<>();
        response.put("componentList", componentPage.getContent());
        response.put("currentPage", pageNo);
        response.put("totalPages", componentPage.getTotalPages());
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

    @DeleteMapping("/component/{componentId}")
    @Transactional
    public ResponseEntity<?> deleteComponent(@PathVariable Long componentId) {
        try {
            componentService.deleteComponentById(componentId);
            return ResponseEntity.ok(Map.of("message", "Component deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete component: " + e.getMessage()));
        }
    }
}
