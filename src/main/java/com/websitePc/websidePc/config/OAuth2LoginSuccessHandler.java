package com.websitePc.websidePc.config;

import com.websitePc.websidePc.dto.TokenPair;
import com.websitePc.websidePc.model.Role;
import com.websitePc.websidePc.model.User;
import com.websitePc.websidePc.repository.TokenRepository;
import com.websitePc.websidePc.repository.UserRepository;
import com.websitePc.websidePc.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    @Value("${app.jwt.refresh-expiration}")
    private long jwtExpirationMs;

    public OAuth2LoginSuccessHandler(JwtService jwtService, UserRepository userRepository, UserDetailsService userDetailsService, TokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
//        khi đăng nhập bằng google thành công thì nó sẽ trả về authentication
//        Lấy thông tin người dùng từ authentication.getPrincipal():
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

//        gán thông tin người dùng vào map
        Map<String, Object> attributes = oAuth2User.getAttributes();

//        lấy email và tên người dùng từ attributes:
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

//        System.out.println("email: " + email + ", name: " + name);

//      tìm kiếm người dùng trong cơ sở dữ liệu theo email:
        User user = userRepository.findByEmail(email)
                .orElse(null);

//        Kiểm tra xem email đã tồn tại trong database chưa
//        Nếu chưa, tạo người dùng mới với thông tin từ OAuth2 và lưu vào database
        if(user == null) {
            user = User.builder()
                    .fullName(name)
                    .email(email)
                    .password("")
                    .role(Role.ROLE_USER) // Mặc định gán role USER
                    .phone("") // khi đăng kí goole mặc định sẽ không có phone
                    .build();
            userRepository.save(user);
        }

//      Nhận userdetails từ UserDetailsService bằng email của người dùng
        UserDetails userDetails =  userDetailsService.loadUserByUsername(user.getEmail());

        // Create new authentication with user details
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

//      Lưu xác thực vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        //  Tạo cặp token (access token và refresh token)
        TokenPair tokens = jwtService.generateTokenPair(newAuth);

//      lưu access token và refresh token vào Redis thông qua TokenRepository
//        để quản lý trạng thái token khi login hoặc logout
        tokenRepository.storeTokens(
                userDetails.getUsername(),
                tokens.getAccessToken(),
                tokens.getRefreshToken()
        );

        // Gửi access token trong response body dạng JSON
//        response.setContentType("application/json");
//        response.getWriter().write("{\"accessToken\": \"" + tokens.getAccessToken() + "\"}");

//        Lưu refresh token vào cookie với thuộc tính HttpOnly để bảo mật
        Cookie cookie = new Cookie("refreshToken", tokens.getRefreshToken());
//        chỉ cho gửi cookie này bằng HTTP, không cho phép truy cập từ JavaScript
        cookie.setHttpOnly(true);
//        Đặt thời gian sống cho cookie là 7 ngày
        cookie.setMaxAge(60*60*24*7);
        cookie.setPath("/");
        cookie.setSecure(false);
//        add cookie vào response
        response.addCookie(cookie);

        String redirectUrl = "http://localhost:5173?access_token="
                + URLEncoder.encode(tokens.getAccessToken(), StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
