package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    private static Map<String, Object> getResponse(int pageNo, Page<Object[]> productPage) {
        Map<String, Object> response = new HashMap<>();
        response.put("productList", productPage.getContent());
        response.put("currentPage", pageNo);
        response.put("totalPages", productPage.getTotalPages());
        return response;
    }


    @GetMapping("/pcListNew/{pageNo}")
    public ResponseEntity<?> pcListNew(@PathVariable int pageNo) {
        int size = 10;
        // Lấy TRANG HIỆN TẠI, không lặp qua các trang trước
        Page<Object[]> productPage = productService.listPcNew(pageNo, size);

        Map<String, Object> response = getResponse(pageNo, productPage);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/laptopListNew/{pageNo}")
    public ResponseEntity<?> laptopListNew(@PathVariable int pageNo) {
        int size = 10;
        // Lấy TRANG HIỆN TẠI, không lặp qua các trang trước
        Page<Object[]> productPage = productService.listLaptopNew(pageNo, size);
        return ResponseEntity.ok(getResponse(pageNo, productPage));
    }

    @GetMapping("/category")
    public ResponseEntity<?> category(
            @RequestParam("pageNo") int pageNo,
            @RequestParam("componentType") String componentType,
            @RequestParam("componentName") String componentName
    ) {
        int size = 10;
        // Lấy TRANG HIỆN TẠI, không lặp qua các trang trước
        Page<Object[]> productPage = productService.categoryCpuVga(pageNo, size, componentType, componentName);
        return ResponseEntity.ok(getResponse(pageNo, productPage));
    }

    @GetMapping("/searchByName")
    public ResponseEntity<?> findProductByName(
            @RequestParam("pageNo") int pageNo,
            @RequestParam("productName") String productName
    ) {
        int size = 10;
        // Lấy TRANG HIỆN TẠI, không lặp qua các trang trước
        Page<Object[]> productPage = productService.findProductByName(pageNo, size, productName);
        return ResponseEntity.ok(getResponse(pageNo, productPage));
    }
}
