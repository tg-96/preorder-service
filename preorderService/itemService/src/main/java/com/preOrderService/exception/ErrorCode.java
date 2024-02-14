package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    OUT_OF_STOCK("재고가 부족합니다."),
    NO_ITEMS("조회할 아이템이 없습니다.");


    private final String message;
    ErrorCode(String message){
        this.message = message;
    }
}
