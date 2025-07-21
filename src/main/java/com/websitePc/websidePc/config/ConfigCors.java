package com.websitePc.websidePc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfigCors implements WebMvcConfigurer {
//    Hàm addCorsMappings được sử dụng để cấu hình CORS (Cross-Origin Resource Sharing)
//    trong một ứng dụng Spring Boot (Java)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        addMapping("/**"): Áp dụng quy tắc CORS cho tất cả các endpoint trong ứng dụng (ký tự /** biểu thị mọi đường dẫn).
//        Nghĩa là mọi yêu cầu HTTP tới ứng dụng đều tuân theo cấu hình này.
        registry.addMapping("/**")
//                Chỉ định nguồn gốc (origin) được phép gửi yêu cầu đến serve
                .allowedOrigins("http://localhost:5173") // URL frontend
//                allowedMethods: Chỉ định các phương thức HTTP được phép. Ở đây, các phương thức GET, POST, PUT, DELETE,
//                và OPTIONS (dùng cho preflight request) được cho phép.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        //        Dòng .allowedHeaders("*") cho phép tất cả các header.
                .allowedHeaders("*")
//                allowCredentials: Cho phép gửi thông tin xác thực (như cookies, HTTP authentication) trong các yêu cầu cross-origin.
//                Điều này quan trọng khi frontend cần gửi token hoặc cookie trong header Authorization.
                .allowCredentials(true)
//                Xác định thời gian (tính bằng giây) mà phản hồi preflight (yêu cầu OPTIONS) được lưu trong cache của trình duyệt.
//                Ở đây là 3600 giây (1 giờ), giúp giảm số lượng yêu cầu preflight lặp lại.
                .maxAge(3600); // Cache preflight response trong 1 giờ
    }
}
