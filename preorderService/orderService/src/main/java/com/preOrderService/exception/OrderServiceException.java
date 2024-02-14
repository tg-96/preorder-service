package com.preOrderService.exception;

import lombok.Getter;

@Getter
public class OrderServiceException extends RuntimeException{
    private final ErrorCode errorCode;
    public OrderServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
