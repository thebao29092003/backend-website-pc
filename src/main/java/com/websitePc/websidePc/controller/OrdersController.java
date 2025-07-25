package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.service.OrdersService;
import com.websitePc.websidePc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// cái controller này cần được bảo vệ nên không thêm request mapping ("/api/public")
@RestController
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService ordersService;
    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/quantityOrderMonths/{month}")
    public Map<String, Object> quantityOrderMonths(@PathVariable int month) {
        List<Object[]> quantityOrderMonth = ordersService.getQuantityOrderMonths(month);
        Map<String, Object> response = new HashMap<>();
        response.put("quantityOrderMonth", quantityOrderMonth);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenueMonths/{month}")
    public Map<String, Object> revenueMonths(@PathVariable int month) {
        List<Object[]> revenueMonths = ordersService.getRevenueMonths(month);
        Map<String, Object> response = new HashMap<>();
        response.put("revenueMonth", revenueMonths);
        return response;
    }

    @GetMapping("/getProductByOrderId")
    public Map<String, Object> getProductByOrderId(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "numberPerPage") int numberPerPage,
            @RequestParam(value = "orderId") String orderId
    ) {
        Page<Object[]> productPage = productService.findProductByOrderId(orderId, page, numberPerPage); // Trả về view hiển thị danh sách product
        Map<String, Object> response = new HashMap<>();
        response.put("productList", productPage.getContent());
        response.put("currentPage", page);
        response.put("totalPages", productPage.getTotalPages());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listOrder")
    public Map<String, Object> listOrders(
            @RequestParam("page") int page,
            @RequestParam("sortDirection") String sortDirection,
            @RequestParam("sortField") String sortField
    ) {
        int size = 10;

//        Chấp nhận tham số "ASC" hoặc "DESC"
        Page<Object[]> orders = ordersService.getListOrder(sortDirection,sortField, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("orderList", orders.getContent());
        response.put("currentPage", page);
        response.put("totalPages", orders.getTotalPages());
        return response;
    }

    @GetMapping("/getOrderByUserId")
    public Map<String, Object> getOrderByUserId(
            @RequestParam("userId") String userId,
            @RequestParam("page") int page,
            @RequestParam("sortDirection") String sortDirection,
            @RequestParam("sortField") String sortField
    ) {
        int size = 10;

//        Chấp nhận tham số "ASC" hoặc "DESC"
        Page<Object[]> orders = ordersService.getOrderByUserId(userId, sortDirection,sortField, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("orderList", orders.getContent());
        response.put("currentPage", page);
        response.put("totalPages", orders.getTotalPages());
        return response;
    }
}
