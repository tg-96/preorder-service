package com.preOrderService.service;

import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.dto.OrdersResponseDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayService {
    private final OrderServiceClient orderServiceClient;

    /**
     * 주문 상태 'PAYMENT_VIEW'인지 확인
     */
    public boolean isPaymentView(Long orderId) {
        //주문 상태 조회
        ResponseEntity<OrdersResponseDto> response = orderServiceClient.getOrderInfo(orderId);
        String orderStatus = response.getBody().getOrderStatus();

        //주문 상태 확인
        if (!orderStatus.equalsIgnoreCase("PAYMENT_VIEW")) {
            return false;
        }
        return true;
    }

    /**
     * 주문 상태 변경 요청
     */
    @Transactional
    public void changeOrderStatus(Long orderId,String status){
        OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto(orderId, status);
        try {
            orderServiceClient.changeStatus(orderStatusRequestDto);
        }catch (Exception e){
            throw new PayServiceException(ErrorCode.CHANGE_ORDER_STATUS_API_ERROR);
        }
    }
}
