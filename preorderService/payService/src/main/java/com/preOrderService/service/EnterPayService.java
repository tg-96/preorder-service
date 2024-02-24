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
     * 주문 생성 요청
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
            //재고 원상 복귀
            log.info("userid:{},주문 생성 요청 실패",payReq.getUserId());

            StockRequest stockReq = new StockRequest(payReq.getItemId(), payReq.getCount());

            log.info("userid:{},캐시 재고 원상 복귀",payReq.getUserId());

            itemServiceClient.addStock(stockReq);

            throw new PayServiceException(ErrorCode.CREATE_ORDER_API_ERROR);
        }
    }

    /**
     * 재고 차감 요청
     */
    @Transactional
    public void requestReduceStock(PayRequestDto payReq) {
        log.info("userId:{}, 재고 차감 요청", payReq.getUserId());

        try {
            StockRequest req = new StockRequest(payReq.getItemId(), payReq.getCount());
            itemServiceClient.reduceStock(req);
        } catch (Exception e) {
            throw new PayServiceException(ErrorCode.REDUCE_STOCK_API_ERROR);
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

    /**
     * 재고가 남았는지 체크
     */
    @Transactional
    public boolean isRemainStock(PayRequestDto req) {
        ResponseEntity<Long> response;
        try {
            response = itemServiceClient.getStockByItemId(req.getItemId());
        } catch (Exception e) {
            throw new PayServiceException(ErrorCode.GET_ITEM_STOCK_API_ERROR);
        }

        Long stock = response.getBody();
        log.info("userId:{}, 남은 재고:{}",req.getUserId(),stock);

        //구매 수 보다 재고가 적으면 예외 처리
        if (stock < req.getCount()) {
            log.info("userId:{}, 재고 부족",req.getUserId());

            return false;
        }
        log.info("userId:{}, 재고 충분",req.getUserId());

        return true;
    }
}