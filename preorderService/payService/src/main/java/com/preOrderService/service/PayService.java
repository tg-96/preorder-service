package com.preOrderService.service;

import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.dto.OrdersResponseDto;
import com.preOrderService.dto.PayRequestDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PayService {
    private final OrderServiceClient orderServiceClient;
    private final ItemServiceClient itemServiceClient;

    /**
     * 주문 상태 'PAYMENT_VIEW'인지 확인
     */
    public boolean isPaymentView(Long orderId) {
        //주문 상태 조회
        try {
            ResponseEntity<OrdersResponseDto> response = orderServiceClient.getOrderInfo(orderId);
            String orderStatus = response.getBody().getOrderStatus();
            log.info("orderId:{}, 주문 상태:{}",orderId,orderStatus);
            //주문 상태 확인
            if (!orderStatus.equalsIgnoreCase("PAYMENT_VIEW")) {
                //결제 취소
                log.info("orderId:{}, 주문 상태:{}, \"PAYMENT_VIEW\" 아니므로 결제 취소",orderId,orderStatus);
                cancelOrder(orderId);
                return false;
            }
            return true;

        } catch (Exception e) {
            throw new PayServiceException(ErrorCode.GET_ORDER_API_ERROR);
        }
    }

    /**
     * 주문 상태 변경 요청
     */
    @Transactional
    public void changeOrderStatus(Long orderId, String status) {
        OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto(orderId, status);
        try {
            orderServiceClient.changeStatus(orderStatusRequestDto);
        } catch (Exception e) {
            throw new PayServiceException(ErrorCode.CHANGE_ORDER_STATUS_API_ERROR);
        }
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 조회
        try {
            log.info("orderId:{}, 주문 취소", orderId);

            ResponseEntity<OrdersResponseDto> response = orderServiceClient.getOrderInfo(orderId);
            OrdersResponseDto order = response.getBody();

            //주문 취소 상태로 변경 요청
            log.info("orderId:{}, 주문 취소 상태로 변경 요청", orderId);
            OrderStatusRequestDto req = new OrderStatusRequestDto(order.getOrderId(), "PAYMENT_CANCEL");
            orderServiceClient.changeStatus(req);

            //재고 예약 취소
            log.info("orderId:{}, 재고 예약 취소", orderId);
            PayRequestDto payRequestDto = new PayRequestDto(order.getUserId(), order.getOrderId(), order.getQuantity());
            itemServiceClient.cancelStock(payRequestDto);
        }
        catch (Exception e) {
            throw new PayServiceException(ErrorCode.ORDER_CANCEL_ERROR);
        }
    }
}