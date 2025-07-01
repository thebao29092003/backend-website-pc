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

//    Thuộc tính orphanRemoval = true chỉ định rằng nếu một thực thể User bị gỡ bỏ khỏi mối quan hệ
//    (tức là trường user được gán giá trị null hoặc bị xóa), thì bản ghi tương ứng của User trong
//    forgot password cũng sẽ bị xóa.
    @OneToOne
    private User user;
}
