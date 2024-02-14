package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    OUT_OF_STOCK("재고가 부족합니다."),
    NO_ITEMS("조회할 아이템이 없습니다."),
    CREATE_ITEM_ERROR("상품 추가 오류"),
    RESERVE_TIME_ERROR("예약한 시간이 현재 시간보다 이전입니다."),
    ITEM_TYPE_ERROR("상품 타입 에러");


    private final String message;
    ErrorCode(String message){
        this.message = message;
    }
}
