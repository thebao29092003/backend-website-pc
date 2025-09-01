package com.websitePc.websidePc.service.ToolForAi;

import com.websitePc.websidePc.dto.AiDto.ProductInput;
import com.websitePc.websidePc.dto.AiDto.UserViewProduct;
import com.websitePc.websidePc.repository.OrdersRepository;
import com.websitePc.websidePc.repository.ProductRepository;
import com.websitePc.websidePc.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
public class AiRecommendTool {
    private final OrdersRepository ordersRepository;
    private final ViewRepository viewRepository;

    @Tool(description = """
            Retrieve products purchased by a user to analyze their preferences.
            Returns a list of ProductInput objects containing
            product_id, product_type, product_price, product_in_stock.
            """)
    public List<ProductInput> getProductUserBuy(
            @ToolParam(description = "User ID to fetch purchase history") String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID người dùng không được để trống.");
        }
        return ordersRepository.productByUser(userId);
    }

    @Tool(description = """
            Retrieve products viewed by a user, including the number of views.
            Returns a list of UserViewProduct objects containing
            product_id, product_in_stock, view_count.
            """)
    public List<UserViewProduct> getProductUserView(
            @ToolParam(description = "User ID to fetch viewed products") String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID người dùng không được để trống.");
        }
        return viewRepository.productByView(userId);
    }

}
