package com.websitePc.websidePc.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
//    mình ràng buộc nó lớn hơn hoặc bằng 0
//    click vào chỗ UN trong chỗ cài đặt bảng ở mysql để sửa lại thành "UNSIGNED"
    private Integer productInStock = 0;

    @Column(nullable = false)
    private BigDecimal productPrice;

    @Column(nullable = false)
    private String productType;

//    khi tạo product mới thì nó sẽ tự động là "TRUE"
//    Khi muốn xóa product thì sẽ set thành "FALSE" (để khỏi hiển thị len trang web)
//    thay vì mình xóa hẳn product khỏi database. Bởi vì khi xóa hẳn thì sẽ mất dữ liệu
//    và phải xóa đi rất nhiều bản ghi liên quan đến product này
    @Column
    private String productActive;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<OrderProduct> orderProducts;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<UserProduct> userProducts;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<Review> reviews;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<ProductComponent> productComponents;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "product", fetch = FetchType.EAGER)
    private Collection<Img> imgs;
}
