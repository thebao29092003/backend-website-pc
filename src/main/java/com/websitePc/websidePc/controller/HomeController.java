package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.service.ComponentService;
import com.websitePc.websidePc.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HomeController {

    private final ProductService productService;
    private final ComponentService componentService;

    public HomeController(ProductService productService, ComponentService componentService) {
        this.productService = productService;
        this.componentService = componentService;
    }

    private static Map<String, Object> getResponse(int pageNo, Page<Object[]> productPage) {
        Map<String, Object> response = new HashMap<>();
        response.put("productList", productPage.getContent());
        response.put("currentPage", pageNo);
        response.put("totalPages", productPage.getTotalPages());
        return response;
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> getProductByPrice(
            @RequestParam("productId") Long productId,
            @RequestParam("price") BigDecimal price,
            @RequestParam("type") String type
            )
    {

        List<Object[]> product =  productService.recommendByPrice(
                productId,
                price,
                BigDecimal.valueOf(5000000),
                type);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("productDetail", product);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/product/{productId}")
    public ResponseEntity<?> getDetailProduct(@PathVariable Long productId) {
        Object product =  productService.getProductById(productId);
        List <Object> component = componentService.getComponentByProductId(productId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("productDetail", product);
        response.put("component", component);
        return ResponseEntity.ok(response);
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
