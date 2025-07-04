package com.websitePc.websidePc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String productName;

    @Column(name = "create_date")
    //    Lấy thời gian hiện tại theo múi giờ hệ thống.
    //Ưu điểm hơn Date: API hiện đại và dễ sử dụng.
    private LocalDate createDate;

//    mặc định là 0, nếu có thì sẽ là số lượng sản phẩm trong kho
    private Integer productInStock = 0;

    @Column(nullable = false)
    private BigDecimal productPrice;

    @Column(nullable = false)
    private String productType;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<OrderProduct> orderProducts;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<CartProduct> cartProducts;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<Review> reviews;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<ProductComponent> productComponents;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<Img> imgs;
}
