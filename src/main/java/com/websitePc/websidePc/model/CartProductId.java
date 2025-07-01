package com.websitePc.websidePc.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CartProductId {
    private Long productId;
    private Long cartId;
}
