package com.websitePc.websidePc.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//lớp cấu hình (PaypalConfig) trong Spring Framework,
// được sử dụng để thiết lập kết nối với PayPal API bằng cách tạo một đối tượng APIContext
@Configuration
public class PaypalConfig {
//    annotation @Value để lấy giá trị từ file cấu hình (thường là application.properties hoặc application.yml)
//    và gán vào các biến clientId, clientSecret, và mode.
    @Value("${paypal.client-id}")
    private String clientId;
    @Value("${paypal.client-secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;

//    Mục đích: Lớp PaypalConfig cấu hình một bean APIContext để Spring quản lý.
//    Đối tượng APIContext này là thành phần cốt lõi trong PayPal SDK, dùng để xác thực và gửi yêu cầu tới PayPal
    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}
