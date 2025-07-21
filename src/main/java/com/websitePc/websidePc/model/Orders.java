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
public class Orders {
//    id này mình sẽ lấy của paypal là paymentID
//    nên phải dùng kiểu String, ngoài ra ko bỏ
//     @GeneratedValue(strategy = GenerationType.IDENTITY) vì
//     nếu bỏ nó sẽ không tạo ra bảng OrderProduct trong databse
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;

    private LocalDate createDate;

    @Column(nullable = false)
    private BigDecimal sumPrice;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "order", fetch = FetchType.EAGER)
    private Collection<OrderProduct> orderProducts;

}
