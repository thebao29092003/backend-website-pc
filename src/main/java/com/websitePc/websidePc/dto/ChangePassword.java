package com.websitePc.websidePc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassword {
    private Integer otp;
    private String email;
    private String newPassword;
    private String repeatPassword;
}
