package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//vì logout cần xác thực người dùng đã đăng nhập, nên tách ra khỏi AuthController
//logout tuy cần token nhưng ban đầu nó gửi prelight để xem có cross hay không nên
// nên trong securityConfig mình phải cho nó permitAll()
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
        return ResponseEntity.ok(Map.of("status", 200));
    }

}
