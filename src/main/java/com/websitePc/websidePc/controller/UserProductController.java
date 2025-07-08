package com.websitePc.websidePc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// UserProductController giống cart
@RestController
@RequestMapping("/api/public")
public class UserProductController {
    private final UserService userService;

    public UserProductController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/product")
    public ResponseEntity<?> userProduct(
            @RequestParam("userId") String userId
    ) {
        List<Object[]> cartItem = userService.productByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("cartList", cartItem);
        return ResponseEntity.ok(response);
    }

//    mỗi lần thay đổi số lượng item trong cart thì phải gọi api này để lưu lại
    @PostMapping("/user/product")
    public ResponseEntity<?> addCartItem(@RequestBody JsonNode cartItemData) {
        Map<String, Object> response = new HashMap<>();
        try{
            // Gọi service để xử lý logic thêm game từ dữ liệu IGDB.
            userService.addCartItem(cartItemData);
            response.put("status", "success");
            response.put("message", "Cart item added successfully");
            // Trả về HTTP 200 OK kèm message thành công.
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("status", "error");
            response.put("message", "Error adding Cart item: " + e.getMessage());
//            Trả về HTTP 500 Internal Server Error.Thêm thông báo lỗi từ exception (e.getMessage()).
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/user/product")
    public ResponseEntity<?> deleteCartItem(@RequestBody JsonNode cartItemData) {
        Map<String, Object> response = new HashMap<>();
        try{
            // Gọi service để xử lý logic thêm game từ dữ liệu IGDB.
            userService.deleteCartItem(cartItemData);
            response.put("status", "success");
            response.put("message", "Cart item deleted successfully");
            // Trả về HTTP 200 OK kèm message thành công.
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("status", "error");
            response.put("message", "Error deleting Cart item: " + e.getMessage());
//            Trả về HTTP 500 Internal Server Error.Thêm thông báo lỗi từ exception (e.getMessage()).
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
