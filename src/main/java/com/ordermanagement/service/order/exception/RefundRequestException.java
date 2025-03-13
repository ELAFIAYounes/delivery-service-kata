package com.ordermanagement.service.order.exception;

public class RefundRequestException extends RuntimeException {
    public RefundRequestException(String message) {
        super(message);
    }

    public RefundRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
