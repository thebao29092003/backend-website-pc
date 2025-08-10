package com.websitePc.websidePc.service;

import com.websitePc.websidePc.dto.LoginRequest;
import com.websitePc.websidePc.dto.RegisterRequest;
import com.websitePc.websidePc.dto.TokenPair;
import com.websitePc.websidePc.exception.ApplicationException;
import com.websitePc.websidePc.model.Role;
import com.websitePc.websidePc.model.User;
import com.websitePc.websidePc.repository.TokenRepository;
import com.websitePc.websidePc.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {


    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    public AuthService(TokenRepository tokenRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        // Kiểm tra xem người dùng đã tồn tại hay chưa
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ApplicationException("EMAIL_TAKEN", "Email is already taken");
        }
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new ApplicationException("PHONE_TAKEN", "Phone is already taken");
        }

//        System.out.println("Registering user: " + registerRequest.getFullName());


//        Tạo một đối tượng User mới từ RegisterRequest
//        ban đầu là role user còn nếu muốn đăng kí là admin thì đường link đó
//        phải được bảo vệ bởi một quyền hạn nhất định

        try {
            User user = User
                    .builder()
                    .fullName(registerRequest.getFullName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(Role.ROLE_USER)
                    .phone(registerRequest.getPhone())
                    .build();

            // Lưu người dùng vào cơ sở dữ liệu
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            throw new ApplicationException("REGISTER_FAILED", "Registration failed.");
        }

    }


    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletResponse response) {
//        authenticationManager.authenticate() to authenticate the user
//        xem note luồng chạy
//        login() → AuthenticationManager.authenticate() → AuthenticationProvider
//        → UserDetailsService -> customUserDetailsService (bởi vì nó implements UserDetailsService)
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

//        set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate access token and refresh token
            TokenPair tokenPair = jwtService.generateTokenPair(authentication);

//            tạo cookie với refresh token
            createCookieWithRefreshToken(tokenPair.getRefreshToken(), response);

            Map<String, Object> responseAccessToken = Map.of(
                    "accessToken", tokenPair.getAccessToken()
            );

//            store the token in redis
//            lấy user đã được xác thực từ authentication chứ ko lấy loginRequest vì đã đc xác thực thì
//            mới lưu vào redis
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//          lưu access token và refresh token vào redis
            tokenRepository.storeTokens(
                    userDetails.getUsername(),
                    tokenPair.getAccessToken(),
                    tokenPair.getRefreshToken()
            );

            return ResponseEntity.ok(responseAccessToken);
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

    }

    public void createCookieWithRefreshToken(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // Prevents JavaScript access to the cookie
        cookie.setPath("/"); // Makes the cookie accessible across the entire application
        cookie.setMaxAge(60 * 60 * 24 * 7); // Sets the cookie to expire in 7 days
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    public TokenPair refreshToken(String refreshToken) {

//        check if it is valid refresh token
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        if (tokenRepository.isRefreshTokenBlacklisted(refreshToken)) {
            throw new RuntimeException("Refresh token is blacklisted");
        }
//        extract email from refresh token
        String user = jwtService.extractEmailFromToken(refreshToken);

//        load user details from userDetailsService equal username
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);


//        if userDetails is null, throw an exception
        if (userDetails == null) {
            throw new RuntimeException("User not found");
        }

//        UsernamePasswordAuthenticationToken là subclass của Authentication
//        ở đây không dùng username, password mà dùng userDetails để tạo Authentication
//        bởi vì userDetails đã được xác thực khi đăng nhập
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

//        generate new access token
        String newAccessToken = jwtService.generateAccessToken(authentication);

//        update the access token in redis
        tokenRepository.removeAccessToken(userDetails.getUsername());

//        update the access token in redis
        tokenRepository.storeTokens(
                userDetails.getUsername(),
                newAccessToken,
                refreshToken
        );
        return new TokenPair(newAccessToken, refreshToken);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username;

        // kiểm tra xem principal có phải là UserDetails không
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
//            nếu nó là một String, có thể là email hoặc tên người dùng thì ép kiểu String
            username = authentication.getPrincipal().toString();
        }

        System.out.println("username in logout: " + username);

//        tìm và xóa cookie refreshToken
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    // Xóa cookie refreshToken
                    cookie.setValue("");
                    cookie.setMaxAge(0); // Đặt thời gian sống của cookie là 0 để xóa nó
                    cookie.setPath("/"); // Đảm bảo cookie có thể được xóa trên toàn bộ ứng dụng
                    response.addCookie(cookie);
                }
            }
        }

//        remove all tokens for the user
        tokenRepository.removeAllTokens(username);
    }

}
