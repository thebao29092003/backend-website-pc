package com.websitePc.websidePc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.dto.ChangePasswordRequest;
import com.websitePc.websidePc.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
   private final UserService userService;

    @GetMapping("/getUserTotalSpent")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getUserTotalSpent(
            @RequestParam("userId") String userId,
            @RequestParam("month") int month
    ) {

        Object user = userService.getUserTotalSpent(userId);
        List<Object[]> spentPerMonth = userService.getSpentPerMonth(userId, month);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("spentPerMonth", spentPerMonth);
        return response;
    }

    @GetMapping("/getUserByName")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getUserByName(
            @RequestParam("userName") String userName,
            @RequestParam("page") int page
    ) {

        int size = 10;
        Page<Object[]> userPage = userService.getUserByName(userName, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("userList", userPage.getContent());
        response.put("currentPage", page);
        response.put("totalPages", userPage.getTotalPages());
        return response;
    }

    @GetMapping("/listUser")
//    phải có quyền admin thì mới truy cập được
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> listOrders(
            @RequestParam("page") int page
    ) {
        int size = 10;

//        Chấp nhận tham số "ASC" hoặc "DESC"
        Page<Object[]> users = userService.listUser(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("userList", users.getContent());
        response.put("currentPage", page);
        response.put("totalPages", users.getTotalPages());
        return response;
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ){
        System.out.println("request changePassword: "+ request);
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok(Map.of("message", "success"));
    }


    @Transactional
    @PostMapping("/infor")
    public ResponseEntity<?> updateUser(@RequestBody JsonNode userData) {
        Map<String, Object> response = new HashMap<>();
//       nếu mà user chỉ đổi phone hoặc fullName thì cái còn lại sẽ truyền cái cũ
//        ko cho đổi email
        try{
            response.put("message", "success");
            userService.updateUser(userData);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("message", "Error: " + e.getMessage());
//            Trả về HTTP 500 Internal Server Error.Thêm thông báo lỗi từ exception (e.getMessage()).
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
