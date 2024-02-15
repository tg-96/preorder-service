package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_FOUND_STOCK("재고 조회가 되지 않습니다.");

    private final String message;
    ErrorCode(String message){
        this.message = message;
    }
}
