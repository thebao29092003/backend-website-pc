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
public class ProductComponent {
    @EmbeddedId
    private ProductComponentId id; //composite key

    @ManyToOne
    @MapsId("productId") // tham chiếu đến trường productId trong ProductComponentId
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("componentId") // tham chiếu đến trường componentId trong ProductComponentId
    @JoinColumn(name = "component_id") // tham chiếu đến trường productId trong OrderProductId
    private Component component;

//    loại link kiện: ram, cpu, mainboard, gpu, ssd, hdd, psu, case
    private String type;
}
