package com.preOrderService.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    OUT_OF_STOCK("재고가 부족합니다."),
    NO_ITEMS("조회할 아이템이 없습니다."),
    CREATE_ITEM_ERROR("상품 추가 오류"),
    RESERVE_TIME_ERROR("예약 시간 오류"),
    ITEM_TYPE_ERROR("상품 타입 오류"),
    ADD_STOCK_ZERO_ERROR("재고 추가는 1개 이상부터 가능합니다."),
    CHANGE_ITEM_INFO_ERROR("상품 정보 변경 중 오류"),
    STOCK_NOT_IN_CACHE("재고 정보가 캐시에 저장되지 않았습니다."),
    REDUCE_STOCK_ZERO_ERROR("재고 감소는 1개 이상부터 가능합니다."),
    FAIL_SYNC_DATABASE("캐시와 RDB의 동기화 실패");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
