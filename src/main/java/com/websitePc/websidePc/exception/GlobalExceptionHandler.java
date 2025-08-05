package com.websitePc.websidePc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Ý nghĩa: Khi ApplicationException xảy ra,
// phương thức handleApplicationException sẽ bắt nó và trả về thông báo lỗi (ví dụ: "Product key already exists") với status 409.
//@RestControllerAdvice: Một annotation của Spring để xử lý ngoại lệ toàn cục cho tất cả
// RestController. Kết hợp @ExceptionHandler,
// nó trả về response trực tiếp khi ngoại lệ được bắt.
@RestControllerAdvice
public class GlobalExceptionHandler {
    //    @ExceptionHandler(ApplicationException.class): Chỉ định phương thức này sẽ xử lý khi ApplicationException được ném.
    @ExceptionHandler(ApplicationException.class)
//    sau này mình muốn thêm lỗi nào ở đây thêm else if là được
    public ResponseEntity<String> handleApplicationException(ApplicationException ex) {
        System.out.println(ex.getErrorType());
        if ("PRODUCT_DUPLICATE".equals(ex.getErrorType())) {
//            ResponseStatus(HttpStatus.CONFLICT): Đặt mã trạng thái HTTP là 409 (Conflict), phù hợp với trường hợp tài nguyên đã tồn tại.
//            và trả về thông báo lỗi từ ex.getMessage() (cái được ném ra khi có lỗi).
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } else if ("EMAIL_TAKEN".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } else if ("PRODUCT_NOT_FOUND".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } else if ("TOKEN_INVALID".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } else if ("TOKEN_MISSING".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } else if("REGISTER_FAILED".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        } else if("INVALID_EMAIL".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } else if("INVALID_OTP".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } else if("PHONE_TAKEN".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } else if("REVIEW_NOT_FOUND".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } else if("WRONG_PASSWORD".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } else if("PASSWORD_NOT_SAME".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } else if("USER_NOT_FOUND".equals(ex.getErrorType())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error");
    }
}
