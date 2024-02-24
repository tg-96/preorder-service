package com.preOrderService.service;

import com.preOrderService.dto.CheckReserveResponseDto;
import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.PayRequestDto;
import com.preOrderService.dto.StockRequest;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EnterPayService {
    private final ItemServiceClient itemServiceClient;
    private final OrderServiceClient orderServiceClient;

    /**
     * 재고 예약 요청
     * response : true -> 재고 예약 완료
     *          : false -> 재고 예약 불가능
     */
    @Transactional
    public boolean reserveStockRequest(PayRequestDto req) {
        log.info("userId:{}"+req.getUserId());
        log.info("itemId:{}"+req.getItemId());
        log.info("stockCount:{}"+req.getCount());

        ResponseEntity<Boolean> response = itemServiceClient.reserveStock(req);

        Boolean reserveStock = response.getBody();
        log.info("재고 예약 여부:{}"+reserveStock);
        return reserveStock;
    }

    /**
     * 주문 생성 요청
     * return : 양수 -> orderId값
     *        : -1 -> 주문 생성 실패 -> 재고 예약 취소
     */
    @Transactional
    public Long requestCreateOrder(PayRequestDto payReq) {

        //주문 생성
        OrderRequestDto req = new OrderRequestDto(payReq.getItemId(), payReq.getUserId(), payReq.getCount());
        try {
            log.info("userid:{},주문 생성 요청",payReq.getUserId());
            ResponseEntity<Long> response = orderServiceClient.createOrder(req);
            return response.getBody();
        } catch (Exception e) {
            //재고 예약 취소
            log.info("userid:{},주문 생성 요청 실패",payReq.getUserId());

            StockRequest stockReq = new StockRequest(payReq.getItemId(), payReq.getCount());

            itemServiceClient.cancelStock(payReq);
            return -1L;
        }
    }

    /**
     * 구매 가능한지 체크: 일반 상품 or 예약 상품이면서 예약시간이 지났는지 확인
     */
    public boolean canPurchaseItem(PayRequestDto requestDto) {
        ResponseEntity<CheckReserveResponseDto> response = itemServiceClient.getItemTypeAndTime(requestDto.getItemId());
        if (response.getBody().getType().equals("reserve")) {
            if (response.getBody().getReserveTime().isAfter(LocalDateTime.now())) {
                return false;
            }
        }
        return true;
    }
}