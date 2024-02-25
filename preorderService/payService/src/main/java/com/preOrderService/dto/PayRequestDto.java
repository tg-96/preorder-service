package com.preOrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayRequestDto {
    private Long userId;
    private Long itemId;
    private Long orderId;
    private Long count;
    public PayRequestDto(EnterPayRequestDto enterReq,Long orderId){
        this.userId = enterReq.getUserId();
        this.itemId = enterReq.getItemId();
        this.count = enterReq.getCount();
        this.orderId = orderId;
    }
}
