package com.preOrderService.service;

import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.dto.OrdersResponseDto;
import com.preOrderService.dto.EnterPayRequestDto;
import com.preOrderService.dto.PayRequestDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PayService {
    private final OrderServiceClient orderServiceClient;
    private final ItemServiceClient itemServiceClient;
    private final RedissonClient redissonClient;


    /**
     * 주문 상태 'PAYMENT_VIEW'인지 확인
     */
    public boolean isPaymentView(PayRequestDto requestDto) {
        //주문 상태 조회
        try {
            ResponseEntity<OrdersResponseDto> response = orderServiceClient.getOrderInfo(requestDto.getOrderId());
            String orderStatus = response.getBody().getOrderStatus();

            log.info("주문상태\nuserId:{}\norderId:{}\norder status:{}",
                    requestDto.getUserId(),requestDto.getOrderId(),orderStatus);

            //주문 상태 확인
            if (!orderStatus.equalsIgnoreCase("PAYMENT_VIEW")) {
                //결제 취소
                log.info("주문 상태가 \"PAYMENT_VIEW\"가 아니라, 결제 취소\n" +
                        "userId:{}, orderId:{}, order status:{},",
                        requestDto.getUserId(),requestDto.getOrderId(),orderStatus);

                cancelOrder(requestDto);
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
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
    public void cancelOrder(PayRequestDto payRequestDto) {
        //Lock 설정
        String lockKey = "lockKey:" + payRequestDto.getItemId();
        RLock lock = redissonClient.getLock(lockKey);

        //주문 조회
        try {
            //락 획득, 무조건 취소 해야 하므로 wait time을 길게 함.
            if (!lock.tryLock(30, 1, TimeUnit.SECONDS)) {
                log.info("cancelOrder\nuserId:{}\norderId:{}\nlock 획득 실패", payRequestDto.getUserId(),payRequestDto.getOrderId());
            }

            log.info("cancelOrder\nuserId:{}\norderId:{}\nlock 획득", payRequestDto.getUserId(),payRequestDto.getOrderId());

            ResponseEntity<OrdersResponseDto> response = orderServiceClient.getOrderInfo(payRequestDto.getOrderId());
            OrdersResponseDto order = response.getBody();

            //주문 취소 상태로 변경 요청
            log.info("cancelOrder\nuserId:{}\norderId:{}\n주문 취소 상태로 변경 요청", payRequestDto.getUserId(),payRequestDto.getOrderId());
            OrderStatusRequestDto req = new OrderStatusRequestDto(order.getOrderId(), "PAYMENT_CANCEL");
            orderServiceClient.changeStatus(req);

            //재고 예약 취소
            log.info("cancelOrder\nuserId:{}\norderId:{}\n재고 예약 취소 요청", payRequestDto.getUserId(),payRequestDto.getOrderId());
            EnterPayRequestDto enterPayRequestDto = new EnterPayRequestDto(order.getUserId(), order.getItemId(), order.getQuantity());
            itemServiceClient.cancelStock(enterPayRequestDto);
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            //락 해제
            lock.unlock();
        }
    }
}