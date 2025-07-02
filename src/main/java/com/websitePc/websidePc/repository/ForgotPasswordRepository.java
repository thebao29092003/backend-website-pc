package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.ForgotPassword;
import com.websitePc.websidePc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    @Query(value = """
        SELECT * FROM forgot_password fp
        WHERE fp.user_user_id = :userId
     """, nativeQuery = true)
    Optional<ForgotPassword> findByUserId(String userId);

    @Modifying
    @Query(value = """
        DELETE FROM forgot_password fp
        WHERE fp.forgot_password_id = :forgotPasswordId
     """, nativeQuery = true)
    void deleteByForGotId(Long forgotPasswordId);

    @Query(value = """  
        SELECT * FROM forgot_password fp
        WHERE fp.otp = :otp AND fp.user_user_id = :userId
     """, nativeQuery = true)
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, String userId);
}
