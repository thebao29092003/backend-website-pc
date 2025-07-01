package com.websitePc.websidePc.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


// OrderProductId là khóa chính tổng hợp (composite primary key)
// dùng để ánh xạ hai trường orderId và productId từ bảng OrderProduct.
@Data
@Embeddable // Chỉ định rằng lớp này có thể được sử dụng làm khóa chính tổng hợp trong một thực thể khác
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Tự động tạo phương thức equals() và hashCode() để so sánh các đối tượng khóa chính.
public class OrderProductId implements Serializable {
    // orderId và productId: Là hai thành phần của khóa chính, đại diện cho mối quan hệ giữa order và product.
    private Long orderId; // Trường khóa chính order_id
    private Long productId; // Trường khóa chính product_id
}
