package com.websitePc.websidePc.filter;

import com.websitePc.websidePc.exception.ApplicationException;
import com.websitePc.websidePc.repository.TokenRepository;
import com.websitePc.websidePc.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//class này được gọi mỗi khi user gửi request đến server
//@Component được thêm vào lớp JwtAuthenticationFilter để Spring Boot tự động phát hiện
// và quản lý bean này trong container.
// Điều này giúp filter được Spring Security sử dụng trong
// chuỗi filter để kiểm tra, xác thực JWT cho mỗi request.
// Nếu không có @Component, filter sẽ không được đăng ký và không hoạt động trong ứng dụng.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;


    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, TokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
//        Intercept the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(request.getRequestURI());
//            nếu request không chứa header Authorization trong lúc đăng nhập, đăng kí
//            (bằng username password và của google) thì mình
//            sẽ cho phép request đó đi qua mà không cần xác thực JWT
            if (
                    request.getRequestURI().contains("/api/auth/") ||
                            request.getRequestURI().contains("/oauth2") ||
                            request.getRequestURI().contains("/favicon.ico") ||
                            request.getRequestURI().contains("/login")||
                            request.getRequestURI().contains("/api/forgot-password")||
                            request.getRequestURI().contains("/api/public")
            ) {
                filterChain.doFilter(request, response); // If no JWT, continue the filter chain
                return;
            }
            // Set 401 Unauthorized status nếu ko có token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else{
            try {
                // Nếu có token, lấy ra từ header Authorization
                // Ví dụ
                jwt = getJwtFromHeader(request);
                if(tokenRepository.isAccessTokenBlacklisted(jwt)){
                    throw new ApplicationException("TOKEN_INVALID", "Token is blacklisted");
                }

                //      Việc kiểm tra token có hợp lệ hay không sẽ được thực hiện trong JwtService
                email = jwtService.extractEmailFromToken(jwt);

                // If the email != null and the user is not authenticated
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if (jwtService.validateTokenForUser(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

//                authToken.setDetails Mục đích:
//                      Khi login lần đầu, Spring Security tự động xử lý việc này trong quá trình xác thực
//                      Khi gửi request với JWT, phải tự thiết lập lại vì đang tạo token xác thực mới từ JWT
//                WebAuthenticationDetailsSource chứa:
//                      Địa chỉ IP của người dùng
//                      Session ID (nếu có)
//                      Các thông tin khác về request HTTP
//                Tại sao cần thiết:
//                      Giúp theo dõi và ghi log về nguồn gốc request
//                      Hữu ích cho việc kiểm tra bảo mật
//                      Cho phép truy cập thông tin request trong các bộ lọc bảo mật khác
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        // Set the authentication in the security context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        filterChain.doFilter(request, response); // Continue the filter chain
                    }
                }
            } catch (ApplicationException e) {
                // Xử lý lỗi token không hợp lệ (bao gồm token hết hạn)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"errorType\": \"" + e.getErrorType() + "\", \"message\": \"" + e.getMessage() + "\"}");
            }

        }


    }

    private String getJwtFromHeader(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        return authHeader.substring(7); // Remove "Bearer " prefix
    }
}
