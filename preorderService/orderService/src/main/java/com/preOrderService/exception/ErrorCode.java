package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    CREATE_ORDER_ERROR("재고가 부족합니다."),
    NO_EXIST_ORDER_ID("해당 주문 id가 존재하지 않습니다."),
    ORDER_STATUS_ERROR("order status가 올바르지 않습니다.");

    private final String message;
    ErrorCode(String message){
        this.message = message;
    }
}
