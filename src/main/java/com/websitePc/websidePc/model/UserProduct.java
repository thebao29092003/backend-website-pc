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
public class UserProduct {
    @EmbeddedId
    private UserProductId id; //composite key

    @ManyToOne
    @MapsId("productId") // tham chiếu đến trường cartId trong UserProductId
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("userId") // tham chiếu đến trường userId trong UserProductId
    @JoinColumn(name = "user_id")
    private User user;


    private Integer quantity;
}
