package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.dto.MailBody;
import com.websitePc.websidePc.exception.ApplicationException;
import com.websitePc.websidePc.model.ForgotPassword;
import com.websitePc.websidePc.model.User;
import com.websitePc.websidePc.repository.ForgotPasswordRepository;
import com.websitePc.websidePc.repository.UserRepository;
import com.websitePc.websidePc.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/forgot-password")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
    }

    //    send mail for email verification
    @PostMapping("/verifyEmail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ApplicationException("INVALID_EMAIL", "Email not found"));
        MailBody mailBody =  MailBody.builder()
                .to(email)
                .subject("OTP cho quên mật khẩu")
                .text("Đây là mã xác thực của bạn: " + otpGenerator())
                .build();

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .user(user)
                .otp(otpGenerator())
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // OTP expires in 5 minutes
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("Email sent successfully with OTP");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(
            @PathVariable Integer otp,
            @PathVariable String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ApplicationException("INVALID_EMAIL", "Email not found"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user.getUserId())
                .orElseThrow(() -> new ApplicationException("INVALID_OTP", "Invalid OTP"));

//        Phương thức getExpirationTime() trả về giá trị của trường expirationTime,
//        là thời điểm mà mã OTP hết hạn.
//        Instant.now() trả về thời điểm hiện tại dưới dạng một đối tượng Instant (thuộc gói java.time),
//        đại diện cho thời gian chính xác theo múi giờ UTC.
//        Date.from(Instant.now()) chuyển đổi Instant hiện tại thành
//        đối tượng java.util.Date để tương thích với expirationTime
//        Phương thức before(...) của lớp java.util.Date kiểm tra xem thời điểm của expirationTime có
//        trước (nhỏ hơn) thời điểm hiện tại hay không.
        if(fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getForgotPasswordId());
            return ResponseEntity.ok("OTP has expired !");
        }

        return ResponseEntity.ok("OTP has been verified !");
    }

    private Integer otpGenerator() {
        Random r = new Random();
//        minimum value is 100000 and maximum value is 999999
        return r.nextInt(100000, 999999);
    }

}
