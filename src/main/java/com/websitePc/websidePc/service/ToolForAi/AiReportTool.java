package com.websitePc.websidePc.service.ToolForAi;

import com.websitePc.websidePc.repository.OrdersRepository;
import com.websitePc.websidePc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiReportTool {
    private final ProductRepository productRepository;
    private final OrdersRepository ordersRepository;

    @Tool(description = "Retrieve the best-selling products in the specified number of months. " +
            "Returns a list of objects containing product_id, product_name, and total_quantity.")
    public List<Object[]> getTopSellingProducts(
            @ToolParam(description = "Number of months to analyze (e.g., 1, 3, 6, 12)") int month,
            @ToolParam(description = "Number of products to analyze (e.g., 1, 2, 3, 5)") int product
    ) {
        if (month <= 0) {
            throw new IllegalArgumentException("Số tháng phải lớn hơn 0.");
        }
        return productRepository.productBuyMonths(month, product);
    }

    @Tool(description = "Retrieve the number of orders per month for the specified number of months.")
    public List<Object[]> getQuantityOrderMonths(
            @ToolParam(description = "Number of months to analyze (e.g., 3, 6, 12)") int month) {
        if (month <= 0) {
            throw new IllegalArgumentException("Số tháng phải lớn hơn 0.");
        }
        return ordersRepository.quantityOrderMonths(month);
    }

    @Tool(description = "Retrieve the total revenue per month for the specified number of months.")
    public List<Object[]> getRevenueMonths(
            @ToolParam(description = "Number of months to analyze (e.g., 3, 6, 12)") int month) {
        if (month <= 0) {
            throw new IllegalArgumentException("Số tháng phải lớn hơn 0.");
        }
        return ordersRepository.revenueMonths(month);
    }

    @Tool(description = "Search for active products by name with pagination, including inventory details. " +
            "Returns a list of objects containing product_id, product_name, product_price, img_link, product_type, and product_in_stock.")
    public List<Object[]> searchProductsInventory(
            @ToolParam(description = "Page number (0-based) for pagination") int page,
            @ToolParam(description = "Number of items per page") int size,
            @ToolParam(description = "Field to sort by (e.g., 'price', 'name', 'stock')") String sortField,
            @ToolParam(description = "Sort order, either 'ASC' for ascending or 'DESC' for descending") String sortDirection) {
        if (page < 0) {
            throw new IllegalArgumentException("Số trang phải lớn hơn hoặc bằng 0.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Kích thước trang phải lớn hơn 0.");
        }
        if (sortField == null || sortField.trim().isEmpty()) {
            throw new IllegalArgumentException("Trường sắp xếp không được để trống.");
        }
        if (!sortDirection.equalsIgnoreCase("ASC") && !sortDirection.equalsIgnoreCase("DESC")) {
            throw new IllegalArgumentException("Thứ tự sắp xếp phải là 'ASC' hoặc 'DESC'.");
        }
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Page<Object[]> result = productRepository.productForAi(PageRequest.of(page, size, sort));
        return result.getContent();
    }
}
