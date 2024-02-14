package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    OUT_OF_STOCK("재고가 부족합니다."),
    NO_ITEMS("조회할 아이템이 없습니다."),
    CREATE_ITEM_ERROR("상품 추가 오류"),
    RESERVE_TIME_ERROR("예약 시간 오류"),
    ITEM_TYPE_ERROR("상품 타입 오류");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
