package com.preOrderService.exception;

import lombok.Getter;

@Getter
public class PayServiceException extends RuntimeException{
    private final ErrorCode errorCode;
    public PayServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
