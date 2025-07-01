package com.websitePc.websidePc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = @Index(name = "product_name_index", columnList = "productId"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String productName;

//    mặc định là 0, nếu có thì sẽ là số lượng sản phẩm trong kho
    private Integer productInStock = 0;

    @Column(nullable = false)
    private String productPrice;

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
}
