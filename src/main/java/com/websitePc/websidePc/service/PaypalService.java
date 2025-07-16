package com.websitePc.websidePc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.websitePc.websidePc.model.Orders;
import com.websitePc.websidePc.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Mục đích: Cung cấp logic để tích hợp thanh toán PayPal vào ứng dụng Spring,
// bao gồm việc tạo yêu cầu thanh toán và thực thi thanh toán sau khi người dùng xác nhận.
@Service
@RequiredArgsConstructor
public class PaypalService {
//    Đối tượng được tiêm từ lớp cấu hình PaypalConfig dùng để xác thực và gửi yêu cầu tới PayPal API.
    private final APIContext apiContext;
    private final OrdersRepository ordersRepository;

//    Chức năng: Tạo một yêu cầu thanh toán PayPal và trả về đối tượng Payment chứa thông tin thanh toán,
//    bao gồm URL để chuyển hướng người dùng đến trang thanh toán PayPal.
    @Transactional
    public Payment createPayment(
            BigDecimal totalUSD,
            String currency,
            String method,
            String intent,
            String cancelUrl,
            String successUrl,
            String userId,
            List<Long> productIds,
            List<Integer> quantities,
            String des,
            BigDecimal totalVND
    ) throws PayPalRESTException {
        List<Transaction> transactions = getTransactionList(totalUSD, currency, des);

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

//        LƯU THÔNG TIN ORDER Ở ĐÂY LÍ DO:
//        Đây là điểm khởi tạo thanh toán,
//        và việc lưu ngay sau khi tạo giúp ghi nhận đơn hàng trước khi người dùng xác nhận.

        addOrder(userId, totalVND);

//        Gửi yêu cầu tới PayPal thông qua payment.create(apiContext) để nhận URL thanh toán.
        return payment.create(apiContext);
    }

    @Transactional
    private void addOrder(String userId, BigDecimal totalVND) {
        // Tạo thời gian hiện tại
        LocalDate createDate = LocalDateTime.now().toLocalDate();
//        1) thêm thông tin order gồm userId, sumPrice và tg tạo order
        ordersRepository.insertOrder(userId, totalVND, createDate);

//     2) Nhận order vừa mới thêm
        Orders order = ordersRepository.findLastInsertedOrder();

    }

    private List<Transaction> getTransactionList(BigDecimal total, String currency, String des) {
//        Tạo đối tượng Amount để xác định số tiền và loại tiền tệ.
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.US, "%.2f", total));

//        Tạo Transaction để mô tả giao dịch (số tiền, mô tả).
        Transaction transaction = new Transaction();
        transaction.setDescription(des);
        transaction.setAmount(amount);

//        Tạo danh sách transactions chứa một hoặc nhiều giao dịch.
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        return transactions;
    }

    //    Chức năng: Hoàn tất thanh toán sau khi người dùng xác nhận trên trang PayPal.
//    Phương thức này thực thi yêu cầu thanh toán đã được tạo bởi createPayment.
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


//        Gửi yêu cầu thực thi tới PayPal qua payment.execute(apiContext, paymentExecution) để hoàn tất giao dịch.
        return payment.execute(apiContext, paymentExecution);
    }
}
