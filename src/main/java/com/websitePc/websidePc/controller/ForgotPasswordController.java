package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.dto.ChangePassword;
import com.websitePc.websidePc.dto.MailBody;
import com.websitePc.websidePc.exception.ApplicationException;
import com.websitePc.websidePc.model.ForgotPassword;
import com.websitePc.websidePc.model.User;
import com.websitePc.websidePc.repository.ForgotPasswordRepository;
import com.websitePc.websidePc.repository.UserRepository;
import com.websitePc.websidePc.service.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/forgot-password")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //    send mail for email verification
    @PostMapping("/verifyEmail")
    @Transactional
    public ResponseEntity<String> verifyEmail(
            @RequestParam("email") String email)
    {
        Integer otp = otpGenerator();
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ApplicationException("INVALID_EMAIL", "Email not found"));

        try {
            Optional<ForgotPassword> forgotPassword = forgotPasswordRepository.findByUserId(user.getUserId());
            // If a forgot password request already exists, delete it
            System.out.println("Forgot password request found: " + forgotPassword.isPresent()+
                    " for user: " + user.getUserId());
            if(forgotPassword.isPresent()) {
                forgotPasswordRepository.deleteByForGotId(forgotPassword.get().getForgotPasswordId());
                forgotPasswordRepository.flush();
            }
            MailBody mailBody =  MailBody.builder()
                    .to(email)
                    .subject("OTP cho quên mật khẩu")
                    .text("Đây là mã xác thực của bạn, hết hạn trong 5 phút: " + otp)
                    .build();

            ForgotPassword forgotPasswordNew = ForgotPassword.builder()
                    .user(user)
                    .otp(otp)
                    .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // OTP expires in 5 minutes
                    .build();

            emailService.sendSimpleMessage(mailBody);
            forgotPasswordRepository.save(forgotPasswordNew);
            return ResponseEntity.ok("Email sent successfully with OTP");
        } catch (ApplicationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody ChangePassword changePassword
    ) {
        User user = userRepository
                .findByEmail(changePassword.getEmail())
                .orElseThrow(() -> new ApplicationException("INVALID_EMAIL", "Email not found"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(
                changePassword.getOtp(),
                        user.getUserId())
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

//        kiểm tra xem mật khẩu mới và mật khẩu lặp lại có giống nhau không
        if(!Objects.equals(changePassword.getNewPassword()
                ,changePassword.getRepeatPassword())){
            return new ResponseEntity<>(
                    "New password does not match",
                    HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Change password successfully !");
    }


    private Integer otpGenerator() {
        Random r = new Random();
//        minimum value is 100000 and maximum value is 999999
        return r.nextInt(100000, 999999);
    }

}
