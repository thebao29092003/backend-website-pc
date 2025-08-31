package com.websitePc.websidePc.model;

import jakarta.persistence.*;

// view nay khác cart nó khi nhận số lần user đã xem sản phẩm
// phục vụ cho Ai gợi ý sản phẩm cá nhân hóa cho user đã đăng nhập
@Entity
public class View {
    @EmbeddedId
    private UserProductId id; //composite key

    @ManyToOne
    @MapsId("productId") // tham chiếu đến trường productId trong UserProductId
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("userId") // tham chiếu đến trường userId trong UserProductId
    @JoinColumn(name = "user_id")
    private User user;

    private Integer timeToView;
}
