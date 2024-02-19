package com.preOrderService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequestDto {
    private Long orderId;
    private String status;
}
