package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_AVAILABLE_TIME_TO_PURCHASE("구매 가능한 시간이 아닙니다."),
    OUT_OF_STOCK("재고가 부족합니다."),
    GET_ITEM_STOCK_API_ERROR("상품의 재고 조회 API 호출에 실패했습니다."),
    REDUCE_STOCK_API_ERROR("실제 재고 감소 API 호출에 실패했습니다."),
    CREATE_ORDER_API_ERROR("주문 생성 API 호출에 실패했습니다.");
    private final String message;
    ErrorCode(String message){
        this.message = message;
    }
}
