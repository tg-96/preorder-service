package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    OUT_OF_STOCK("재고가 부족합니다.");

    private final String message;
    ErrorCode(String message){
        this.message = message;
    }
}
