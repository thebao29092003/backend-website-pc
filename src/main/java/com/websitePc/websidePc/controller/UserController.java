package com.websitePc.websidePc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.dto.ChangePasswordRequest;
import com.websitePc.websidePc.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
   private final UserService userService;

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
