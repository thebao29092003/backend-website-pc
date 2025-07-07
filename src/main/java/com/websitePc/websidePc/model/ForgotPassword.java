package com.websitePc.websidePc.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long forgotPasswordId;

    @Column(nullable = false)
    private Integer otp;

    @Column(nullable = false)
    private Date expirationTime;

// trong my sql thi user_user_id la khoa ngoai
//  forgot password và user trong mysql nó ko có kí hiệu quan hệ 1:1
//    nên nó biểu diễn 1 nhiều và dùng ràng buộc 1 user chỉ có 1 otp
    @OneToOne
    private User user;
}
