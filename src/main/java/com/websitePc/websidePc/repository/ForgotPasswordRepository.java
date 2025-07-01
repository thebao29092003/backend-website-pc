package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.ForgotPassword;
import com.websitePc.websidePc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {

    @Query(value = """  
        SELECT * FROM forgot_password fp
        WHERE fp.otp = :otp AND fp.user_id = :userId
     """, nativeQuery = true)
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, String userId);
}
