package com.preOrderService.exception;

import lombok.Getter;

@Getter
public class ItemServiceException extends RuntimeException{
    private final ErrorCode errorCode;
    public ItemServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
