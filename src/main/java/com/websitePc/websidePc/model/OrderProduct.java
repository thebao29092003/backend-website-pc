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
public class OrderProduct {
    @EmbeddedId
    private OrderProductId id; //composite key

    @ManyToOne
    @MapsId("orderId") // tham chiếu đến trường orderId trong OrderProductId
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne
    @MapsId("productId") // tham chiếu đến trường productId trong OrderProductId
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
}
