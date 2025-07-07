package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//vì logout cần xác thực người dùng đã đăng nhập, nên tách ra khỏi AuthController
@RestController
@RequestMapping("/api")
public class LogoutController {
    private final AuthService authService;

    public LogoutController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
                                    HttpServletRequest request,
                                    HttpServletResponse response
    ) {
        authService.logout(request, response);
        return ResponseEntity.ok("Logged out successfully");
    }

}
