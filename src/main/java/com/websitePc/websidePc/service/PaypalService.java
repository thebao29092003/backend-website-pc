package com.websitePc.websidePc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.websitePc.websidePc.model.Orders;
import com.websitePc.websidePc.repository.OrderProductRepository;
import com.websitePc.websidePc.repository.OrdersRepository;
import com.websitePc.websidePc.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Long.parseLong;

//Mục đích: Cung cấp logic để tích hợp thanh toán PayPal vào ứng dụng Spring,
// bao gồm việc tạo yêu cầu thanh toán và thực thi thanh toán sau khi người dùng xác nhận.
@Service
@RequiredArgsConstructor
public class PaypalService {
    //    Đối tượng được tiêm từ lớp cấu hình PaypalConfig dùng để xác thực và gửi yêu cầu tới PayPal API.
    private final APIContext apiContext;
    private final OrdersRepository ordersRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    //    Chức năng: Tạo một yêu cầu thanh toán PayPal và trả về đối tượng Payment chứa thông tin thanh toán,
//    bao gồm URL để chuyển hướng người dùng đến trang thanh toán PayPal.
//    luồng chạy của service:
//    1 khi user bấm thanh toán tạo transaction chứa thông tin từng item(tên, đơn giá, số lượng) và tổng tiền (USD)
//    2 tạo payer, intent, và transactions (lấy từ hàm getTransactionList) cho payment
//    3 add order gồm thông tin: userId, danh sách sản phẩm và orderId lấy paymentId được tạo ở bước 2
//    order hiện có status là CANCELED là thành toán chưa được hoàn tất
//    chỉ khi controller gọi dến method executePayment th order sẽ chuyển sang trạng thái COMPLETED
    @Transactional
    public Payment createPayment(
            String currency,
            String method,
            String intent,
            String cancelUrl,
            String successUrl,
            String userId,
            JsonNode productListInput
    ) throws PayPalRESTException, IOException {

        List<Transaction> transactions = getTransactionList(productListInput, currency);

//        Tạo Payer để xác định phương thức thanh toán (ví dụ: "paypal").
        Payer payer = new Payer();
        payer.setPaymentMethod(method);

//        Tạo Payment với mục đích thanh toán (intent) và thông tin người thanh toán (payer), thông tin transactions.
        Payment payment = new Payment();

//        setIntent: Mục đích thanh toán (ví dụ: "sale", "authorize", "order").
//        "sale" cho thanh toán ngay, "authorize" cho giữ tiền, "order" cho đặt hàng.
//        xem note
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

//        Thiết lập RedirectUrls để xử lý trường hợp người dùng hủy hoặc hoàn thành thanh toán.
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        Payment createPayment = payment.create(apiContext);
//        LƯU THÔNG TIN ORDER Ở ĐÂY LÍ DO:
//        Đây là điểm khởi tạo thanh toán,
//        và việc lưu ngay sau khi tạo giúp ghi nhận đơn hàng trước khi người dùng xác nhận.
//        ở đây đơn hàng ở trạng thái chưa hoàn thành, orderId mình sẽ lấy id của paypal
        addOrder(userId, productListInput, createPayment.getId());

//        Gửi yêu cầu tới PayPal thông qua payment.create(apiContext) để nhận URL thanh toán.
        return createPayment;
    }

    @Transactional
    public void addOrder(String userId, JsonNode productListInput, String orderId) throws IOException {

//        convert từ jsonNode có dạng là 1 mảng object sang list kiểu map
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> productList = objectMapper.readValue(
                productListInput.traverse(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

//        tính tổng tiền VND
        BigDecimal totalVND = BigDecimal.ZERO;
//        duyệt qua từng map trong list map
        for (Map<String, Object> product : productList) {
            BigDecimal priceVND = new BigDecimal(product.get("price").toString());
            Integer quantityUserBuy = (Integer) product.get("quantityUserBuy");
            // Tính tổng tiền VND cho item (giá USD × số lượng)
            BigDecimal itemTotalVND = priceVND.multiply(new BigDecimal(quantityUserBuy));
            totalVND = totalVND.add(itemTotalVND);
        }

        // Tạo thời gian hiện tại
        LocalDate createDate = LocalDateTime.now().toLocalDate();
//        1) thêm thông tin order gồm userId, sumPrice và tg tạo order
//        trạng thái là CANCELED
        ordersRepository.insertOrder(userId, totalVND, createDate, orderId, "CANCELED");

//     2) Nhận order vừa mới thêm
        Orders order = ordersRepository.findOrderByOrderId(orderId);

//        duyệt qua từng Map trong productList.
        for (Map<String, Object> product : productList) {
            Long productId = parseLong((product.get("productId").toString()));
            Integer quantityUserBuy = (Integer) product.get("quantityUserBuy");
            System.out.println("productList: " + productList);

//                Lưu thông tin sản phẩm (quantity, orderId, productIdLong) vào bảng order_product.
            orderProductRepository.insertOrderProduct(quantityUserBuy, order.getOrderId(), productId);
        }
    }

    private List<Transaction> getTransactionList(JsonNode productListInput, String currency) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> productList = objectMapper.readValue(
                productListInput.traverse(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        System.out.println("productList: " + productList);
        // Tạo ItemList để chứa danh sách sản phẩm
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        BigDecimal totalUSD = BigDecimal.ZERO;
        // Tỷ giá (1 USD = 26000 VND)
        BigDecimal exchangeRate = new BigDecimal("26000");

//        duyệt qua từng Map trong productList.
        for (Map<String, Object> product : productList) {
            String des = (String) product.get("des");
            Integer quantityUserBuy = (Integer) product.get("quantityUserBuy");
            BigDecimal priceVND = new BigDecimal(product.get("price").toString());

            // Chuyển đổi giá sang USD (giả sử tỷ giá 1 USD = 26000 VND)
            BigDecimal priceUSD = priceVND.divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);

            // Tính tổng tiền USD cho item (giá USD × số lượng)
            BigDecimal itemTotalUSD = priceUSD.multiply(new BigDecimal(quantityUserBuy));

            // Cộng vào tổng tiền
            totalUSD = totalUSD.add(itemTotalUSD);

//            System.out.println("des: " + des);
//            System.out.println("quantity: " + quantityUserBuy);
//            System.out.println("priceVND: " + priceVND);
//            System.out.println("priceUSD: " + priceUSD);
//            System.out.println("totalUSD: " + totalUSD);

            // Tạo Item cho PayPal
            Item item = new Item();
//            tên cho từng item (mô tả trong notification của người bán paypal
            item.setName(des)
                    .setCurrency(currency)
                    .setPrice(String.format(Locale.US, "%.2f", priceUSD))
                    .setQuantity(String.valueOf(quantityUserBuy));
            items.add(item);
        }
        itemList.setItems(items);

//          Chuyển đổi sang USD (chia cho 26,000)
//            ở đây vì paypal ko hỗ trợ tiền việt trong sandbox nên mình sẽ chuyê
//            từ tiền việt sang tiền đô USD
//            mỗi giao dịch paypal sẽ mất 1 khoảng phí giao dịch
//        Tạo đối tượng Amount để xác định số tiền và loại tiền tệ.
//        amount chính là tổng tiền
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.US, "%.2f", totalUSD));

//        Tạo Transaction để mô tả giao dịch (số tiền, mô tả).
//        tính cart có nhiều item thì mình thêm nhiều transction ở đây thôi
//        như vậy phải xem lại cách truyền body ở frontend chỗ des với totalVND
//        chỗ productDetail là đúng rồi những chỗ cartPage xem lại
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setItemList(itemList);

//        Tạo danh sách transactions chứa một hoặc nhiều giao dịch.
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        return transactions;
    }

    //    Chức năng: Hoàn tất thanh toán sau khi người dùng xác nhận trên trang PayPal.
//    Phương thức này thực thi yêu cầu thanh toán đã được tạo bởi createPayment.
    @Transactional
    public Payment executePayment(
            String paymentId,
            String payerId
    ) throws PayPalRESTException {
//        Tạo đối tượng Payment với paymentId (ID của thanh toán được tạo trước đó).
        Payment payment = new Payment();
        payment.setId(paymentId);

//        Tạo PaymentExecution với payerId (ID của người thanh toán, do PayPal cung cấp sau khi người dùng xác nhận).
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

//      CẬP NHẬT TRẠNG THÁI ORDER (getState()) Ở ĐÂY GỒM payerId (nID người thanh toán trên paypal)

        // Thực thi thanh toán
        Payment executedPayment = payment.execute(apiContext, paymentExecution);

        // Kiểm tra trạng thái thanh toán
        if ("approved".equals(executedPayment.getState())) {
            // Tìm đơn hàng bằng paymentId vì trước đó
            // mình lấy paymentId làm orderId
            Orders order = ordersRepository.findOrderByOrderId(paymentId);
            if (order != null) {
                // Cập nhật trạng thái đơn hàng và payerId
                order.setStatus("COMPLETED");
                ordersRepository.save(order);

                // Trừ hàng trong kho
                List<Object[]> orderProducts = orderProductRepository.findByOrderId(order.getOrderId());
                System.out.println("orderProducts: " + orderProducts);
                for (Object[] product : orderProducts) {
                    System.out.println("product: " + Arrays.toString(product));
                    Long productId = (Long) product[0];
                    Integer quantityUserBuy = (Integer) product[1];
//                Cập nhật số lượng tồn kho (inStock) của sản phẩm trong bảng product dựa trên productIdLong và quantity.
                    productRepository.updateInStockByProductId(productId, quantityUserBuy);
                }
            }
        }

//        Gửi yêu cầu thực thi tới PayPal qua payment.execute(apiContext, paymentExecution) để hoàn tất giao dịch.
        return executedPayment;
    }
}
