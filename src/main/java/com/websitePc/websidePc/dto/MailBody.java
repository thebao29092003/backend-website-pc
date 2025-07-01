package com.websitePc.websidePc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MailBody {
//  người nhận mail
    private String to;
//    đối tượng email (otp)
    private String subject;
//    nội dung email
    private String text;
}
