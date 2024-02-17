package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    CREATE_ORDER_ERROR("재고가 부족합니다.");

    private final String message;
    ErrorCode(String message){
        this.message = message;
    }
}
