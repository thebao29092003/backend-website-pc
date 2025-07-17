package com.websitePc.websidePc.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.websitePc.websidePc.service.PaypalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
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

            String userId = orderData.get("userId").asText();
            JsonNode productListInput = orderData.get("productList");
            System.out.println("userId: "+ userId);
//            [
//            {"productId":65,"quantityUserBuy":1,"price":42000000,"des":"PC Intel Core i9 RTX 3090"},
//            {"productId":83,"quantityUserBuy":1,"price":23000000,"des":"PC Intel Core i7 GTX 1080 Ti"}
//            ]
            System.out.println("productList: "+ productListInput);

            Payment payment = paypalService.createPayment(
                    "USD",
                    "paypal",
                    "sale",
                    cancelUrl,
                    successUrl,
                    userId,
                    productListInput
            );

//            System.out.println("payment: "+ payment);
//            System.out.println("payment.getLinks(): "+ payment.getLinks());

//            Nhận đối tượng Payment từ PayPal, chứa danh sách Links.
//            Tìm approval_url trong payment.getLinks() để chuyển hướng người dùng đến trang thanh toán PayPal.
            for(Links link : payment.getLinks()){
                if(link.getRel().equals("approval_url")){
//                    Thành công: Trả về RedirectView đến approval_url của PayPal, đưa người dùng đến trang xác nhận thanh toán.
                    return ResponseEntity.ok(Map.of("approvalUrl", link));
                }
            }
//        đổi này lại thành PayPalRESTException
        } catch (PayPalRESTException e){
//            Nếu có lỗi (PayPalRESTException), ghi log và chuyển hướng đến /payment/error.
            log.error(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
