package com.websitePc.websidePc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct {
    @EmbeddedId
    private CartProductId id; //composite key

    @ManyToOne
    @MapsId("cartId") // tham chiếu đến trường cartId trong CartProductId
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @MapsId("productId") // tham chiếu đến trường cartId trong CartProductId
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
}
