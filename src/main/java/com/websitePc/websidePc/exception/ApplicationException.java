package com.websitePc.websidePc.exception;
import lombok.Getter;

//Mô tả: Đây là một ngoại lệ tùy chỉnh kế thừa từ RuntimeException,
// được thiết kế để báo lỗi khi có vấn đề cụ thể
@Getter
public class ApplicationException extends RuntimeException {
    private final String errorType;
    private final String message;

//    Constructor nhận String message để truyền thông báo lỗi,
//    sau đó gọi super(message) để truyền lên RuntimeException.
    public ApplicationException(String errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.message = message;
    }

}