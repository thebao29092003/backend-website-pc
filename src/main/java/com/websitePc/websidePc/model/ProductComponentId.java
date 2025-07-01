package com.websitePc.websidePc.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
@EqualsAndHashCode
public class ProductComponentId implements Serializable {
    private Long productId;
    private Long componentId;
}
