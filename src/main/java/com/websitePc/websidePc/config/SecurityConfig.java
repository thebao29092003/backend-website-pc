package com.websitePc.websidePc.config;

import com.websitePc.websidePc.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity: Kích hoạt cấu hình bảo mật web của
// Spring Security cho ứng dụng, cho phép bạn tùy chỉnh các quy tắc bảo mật HTTP.
@EnableWebSecurity
//@EnableMethodSecurity: Bật bảo mật ở mức phương thức (method-level), cho phép sử dụng các annotation như @PreAuthorize,
// @Secured để kiểm soát truy cập từng phương thức trong code.
@EnableMethodSecurity
public class SecurityConfig {
    //    UserDetailsService: Loads user details from the database
    private final UserDetailsService userDetailsService;

    //    JwtAuthenticationFilter: Custom filter for JWT authentication
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //    OAuth2LoginSuccessHandler: Handles successful OAuth2 (Google) logins
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    //   constructor injection for dependencies
    public SecurityConfig(UserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    //  Configures BCrypt password hashing for secure password storage.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        //    Uses UserDetailsService to load user data
        authenticationProvider.setUserDetailsService(userDetailsService);

        //    Uses BCryptPasswordEncoder for password verification
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    //    Manages the authentication process
//    Uses the configured AuthenticationProvider
//    Handles authentication requests during login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Trả về một AuthenticationManager sử dụng AuthenticationProvider đã cấu hình
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                // Tắt CSRF protection
                .csrf(csrf -> csrf.disable())
                // Sử dụng Stateless session management
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                xác thực http requests những request nào không cần xác thực, những request nào cần xác thực
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                "/api/auth/**", "/oauth2/**", "/login/oauth2/code/google",
                                "/favicon.ico", "/login/**", "/api/forgot-password/**",
                                "/api/public/**", "api/logout")
                        .permitAll()
                        .anyRequest().authenticated() // Yêu cầu xác thực cho tất cả các yêu cầu khác
                )

//                cấu hình để đăng nhập bằng google
//                khi dang nhập thành công, sẽ gọi oAuth2LoginSuccessHandler để xử lý
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                )


                // Thêm JwtAuthenticationFilter vào chuỗi bộ lọc trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

//                Thêm này thì những jwt nào ko xác thực được thì nó Báo lỗi response chứ không redirect về
//                trang đăng nhập của google nữa
//                Thêm JwtAuthenticationFilter trước OAuth2AuthorizationRequestRedirectFilter
                .addFilterBefore(jwtAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)

                // Cấu hình AuthenticationProvider để sử dụng DaoAuthenticationProvider
                .authenticationProvider(authenticationProvider())

//                Người dùng đã được xác thực (có token JWT hợp lệ), nhưng không có vai trò
//                cần thiết (ví dụ: không phải ADMIN) để truy cập một endpoint được bảo vệ
//                bằng annotation như @PreAuthorize("hasRole('ADMIN')").
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request,
                                              response,
                                              authException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Access Denied - You do not have ADMIN rights.");
                        })
                );
        return http.build();
    }
}
