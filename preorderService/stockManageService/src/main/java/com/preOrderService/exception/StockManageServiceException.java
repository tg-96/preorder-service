package com.preOrderService.exception;

import lombok.Getter;

@Getter
public class StockManageServiceException extends RuntimeException{
    private final ErrorCode errorCode;
    public StockManageServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
