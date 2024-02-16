package com.preOrderService.entity;

public enum OrderStatus {
    PRODUCT_VIEW("상품 화면 상태"),
    PAYMENT_VIEW("결제 화면 상태"),
    PAYMENT_IN_PROGRESS("결제 중 상태"),
    PAYMENT_COMPLETED("결제 완료 상태");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
