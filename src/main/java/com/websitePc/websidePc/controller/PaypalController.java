package com.websitePc.websidePc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.websitePc.websidePc.service.PaypalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/public")
public class PaypalController {
//    Dịch vụ PayPal được tiêm để gọi các phương thức createPayment và executePayment
    private final PaypalService paypalService;

//    Xử lý yêu cầu POST để tạo thanh toán PayPal và chuyển hướng người dùng đến trang thanh toán của PayPal.
    @PostMapping("/payment/create")
    public ResponseEntity<?> paypal(
            @RequestBody JsonNode orderData
    ) {
        try{
//            Gọi paypalService.createPayment với các tham số:
//              total2: Số tiền (10,000,000).
//              "Đ": Loại tiền tệ (lưu ý: đây là lỗi, sẽ giải thích ở phần VND).
//              "paypal": Phương thức thanh toán.
//              "sale": Mục đích thanh toán (thanh toán ngay).
//              "Payment description": Mô tả giao dịch.
//              cancelUrl và successUrl: URL chuyển hướng khi hủy hoặc thành công.

            String cancelUrl = "http://localhost:5173";
            String successUrl = "http://localhost:5173/payment";

            String userId = orderData.get("userId").toString();
            BigDecimal totalVND = new BigDecimal(orderData.get("totalVND").toString());
            List<Long> productIds = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();

//            đang xử lí
            JsonNode productIdQuantity = orderData.get("productIdQuantity");
//            productIds trả về mảng object có productId làm ket và value là quantity
//            [{64: 3}, {55: 10}]

            String des = orderData.get("des").toString();

            System.out.println("userId: "+ userId);
            System.out.println("totalVND: "+ totalVND);
            System.out.println("productIdQuantity: "+ productIdQuantity);
            System.out.println("des: "+ des);

//            // Chuyển đổi sang USD (chia cho 26,000)
//            BigDecimal exchangeRate = new BigDecimal("26000");
//            BigDecimal totalUSD = totalVND.divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);
//
////            ở đây vì paypal ko hỗ trợ tiền việt trong sandbox nên mình sẽ chuyê
////            từ tiền việt sang tiền đô USD
////            mỗi giao dịch paypal sẽ mất 1 khoảng phí giao dịch
//            Payment payment = paypalService.createPayment(
//                    totalUSD,
//                    "USD",
//                    "paypal",
//                    "sale",
//                    cancelUrl,
//                    successUrl,
//                    userId,
//                    productIds,
//                    quantities,
//                    des,
//                    totalVND
//            );
//
//            System.out.println("payment: "+ payment);
//            System.out.println("payment.getLinks(): "+ payment.getLinks());
//
////            Nhận đối tượng Payment từ PayPal, chứa danh sách Links.
////            Tìm approval_url trong payment.getLinks() để chuyển hướng người dùng đến trang thanh toán PayPal.
//            for(Links link : payment.getLinks()){
//                if(link.getRel().equals("approval_url")){
////                    Thành công: Trả về RedirectView đến approval_url của PayPal, đưa người dùng đến trang xác nhận thanh toán.
//                    return ResponseEntity.ok(Map.of("approvalUrl", link));
//                }
//            }
            return ResponseEntity.ok("test");
//            đổi trỗ này thành PayPalRESTException sau khi test
        } catch (Exception e){
//            Nếu có lỗi (PayPalRESTException), ghi log và chuyển hướng đến /payment/error.
            log.error(e.getMessage());
        }
        return  ResponseEntity.badRequest().build();
    }

//    Xử lý yêu cầu GET khi người dùng xác nhận thanh toán trên PayPal và được chuyển hướng về successUrl.
//    Nhận paymentId và payerId từ query parameters (do PayPal cung cấp sau khi người dùng xác nhận).
    @GetMapping("/payment/success")
    public ResponseEntity<?> paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        try{
//            Gọi paypalService.executePayment để hoàn tất thanh toán.
            Payment payment = paypalService.executePayment(paymentId, payerId);
            Map<String, Object> response = new HashMap<>();
//            Kiểm tra trạng thái thanh toán (payment.getState()). Nếu là "approved", trả về chuỗi "paymentSuccess".
            if(payment.getState().equals("approved")){
                response.put("status", 200);
                response.put("message", "Thanh toán thành công");
                return ResponseEntity.ok(response);
            }
        } catch (PayPalRESTException e) {
            log.error(e.getDetails().toString());
        }
//        mình dùng api thì chỗ này trả về json
        return  ResponseEntity.badRequest().build();
    }



}
