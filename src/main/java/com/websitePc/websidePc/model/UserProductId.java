package com.websitePc.websidePc.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class UserProductId {
//    phải trung kiểu dữ liệu của productId trong bảng Product
    private Long productId;
    private String userId;
}
