package com.websitePc.websidePc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "component_id")
    private Long componentId;

    @Column(nullable = false)
    private String componentName;


    @Column(nullable = false)
    private String componentType;


    //    khi tạo component mới thì nó sẽ tự động là "TRUE"
//    Khi muốn xóa component thì sẽ set thành "FALSE" (để khỏi hiển thị len khi admin thêm
//    linh kiện cho sản phẩm)
//    thay vì mình xóa hẳn component khỏi database. Bởi vì khi xóa hẳn thì sẽ mất dữ liệu
//    và phải xóa đi rất nhiều bản ghi liên quan đến component này
    @Column
    private String componentActive;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "component", fetch = FetchType.EAGER)
    private Collection<ProductComponent> productComponents;

}
