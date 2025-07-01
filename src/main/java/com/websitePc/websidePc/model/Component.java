package com.websitePc.websidePc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
    private BigDecimal componentPrice;

//    lưu vào database dưới dạng JSON
//    Sử dụng @Column(columnDefinition = "JSON") cho component_specification
//    là phù hợp vì các linh kiện PC có thông số không đồng nhất
    @Column(columnDefinition = "JSON", nullable = false)
    private String componentSpecification;

    @Column(nullable = false)
    private String componentType;

    private Integer componentInStock;

}
