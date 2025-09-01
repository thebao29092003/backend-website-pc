package com.websitePc.websidePc.dto.AiDto;

import java.math.BigDecimal;

public record UserViewProduct(Long product_id,
                              Integer product_in_stock,
                              Integer view_count) {
}