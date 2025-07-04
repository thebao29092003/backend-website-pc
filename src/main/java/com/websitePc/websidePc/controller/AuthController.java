package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.dto.LoginRequest;
import com.websitePc.websidePc.dto.RegisterRequest;
import com.websitePc.websidePc.dto.TokenPair;
import com.websitePc.websidePc.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
//        save the  new user to the database and return a success response
        System.out.println("Registering user: " + registerRequest);
        authService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return authService.login(loginRequest, response);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        TokenPair tokenPair = authService.refreshToken(refreshToken);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", tokenPair.getAccessToken());
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok("Logged out successfully");
    }


}