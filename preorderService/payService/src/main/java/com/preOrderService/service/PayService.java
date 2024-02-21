package com.preOrderService.service;

import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.dto.OrdersResponseDto;
import com.preOrderService.dto.StockRequest;
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
    private final ItemServiceClient itemServiceClient;

    /**
     * 주문 상태 'PAYMENT_VIEW'인지 확인
     */
    public boolean isPaymentView(Long orderId) {
        //주문 상태 조회
        try {
            ResponseEntity<OrdersResponseDto> response = orderServiceClient.getOrderInfo(orderId);
            String orderStatus = response.getBody().getOrderStatus();

            //주문 상태 확인
            if (!orderStatus.equalsIgnoreCase("PAYMENT_VIEW")) {

                //결제 취소
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
            ResponseEntity<OrdersResponseDto> response = orderServiceClient.getOrderInfo(orderId);
            OrdersResponseDto order = response.getBody();

            //주문 취소 상태로 변경 요청
            OrderStatusRequestDto req = new OrderStatusRequestDto(order.getOrderId(), "PAYMENT_CANCEL");
            orderServiceClient.changeStatus(req);

            //재고 롤백
            StockRequest stockRequest = new StockRequest(order.getItemId(), order.getQuantity());
            itemServiceClient.addStock(stockRequest);
        }
        catch (Exception e) {
            throw new PayServiceException(ErrorCode.ORDER_CANCEL_ERROR);
        }
    }
}
