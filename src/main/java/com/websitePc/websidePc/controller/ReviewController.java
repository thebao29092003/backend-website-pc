package com.websitePc.websidePc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.repository.ReviewRepository;
import com.websitePc.websidePc.service.ReviewService;
import com.websitePc.websidePc.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/reviewList")
    public ResponseEntity<?> reviewList(@RequestParam("page") int pageNo,
                                          @RequestParam("productId") Long productId) {
        int size = 20;
        // Lấy TRANG HIỆN TẠI, không lặp qua các trang trước
        Page<Object[]> reviewPage = reviewService.getReviewByProductId(productId, pageNo, size);

        Map<String, Object> response = new HashMap<>();
        response.put("reviewList", reviewPage.getContent());
        response.put("currentPage", pageNo);
        response.put("totalPages", reviewPage.getTotalPages());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/addReview")
    public ResponseEntity<?> addReview(@RequestBody JsonNode reviewData) {
        Map<String, Object> response = new HashMap<>();
        try{
            // Gọi service để xử lý logic thêm product từ dữ liệu IGDB.
            reviewService.addReview(reviewData);
            response.put("status", "success");
            response.put("message", "Review product added successfully");
            // Trả về HTTP 200 OK kèm message thành công.
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("status", "error");
            response.put("message", "Error adding review product: " + e.getMessage());
//            Trả về HTTP 500 Internal Server Error.Thêm thông báo lỗi từ exception (e.getMessage()).
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/updateReview")
    public ResponseEntity<?> updateReview(@RequestBody JsonNode updateData) {
        Map<String, Object> response = new HashMap<>();
        try {
            reviewService.updateReview(updateData);
            response.put("status", "success");
            response.put("message", "Review product updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updated Review product: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/deleteReview")
    public ResponseEntity<?> deleteReview(@RequestParam("reviewId") Long reviewId) {
        Map<String, Object> response = new HashMap<>();
        try {
            reviewService.deleteReviewById(reviewId);
            response.put("status", "success");
            response.put("message", "Review product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleted Review product: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/hasUserBuyProduct")
    public ResponseEntity<?> hasUserBuyProduct(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "productId") Long productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long hasBuyProduct =  userService.hasUserBuyProduct(userId, productId);
            response.put("status", "success");
            if(hasBuyProduct == 1) {
                response.put("hasBuyProduct", true);
            } else{
                response.put("hasBuyProduct", false);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
