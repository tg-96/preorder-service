package com.preOrderService.service;

import com.preOrderService.dto.CheckReserveResponseDto;
import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.EnterPayRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EnterPayService {
    private final ItemServiceClient itemServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final RedissonClient redissonClient;

    /**
     * 재고 예약 요청
     * response : true -> 재고 예약 완료
     * : false -> 재고 예약 불가능
     */
    @Transactional
    public boolean reserveStockRequest(EnterPayRequestDto req) {
        //Lock 설정
        String lockKey = "lockKey:" + req.getItemId();
        RLock lock = redissonClient.getLock(lockKey);
        Boolean reserveStock = false;

        try {
            //락 획득
            if (!lock.tryLock(10, 1, TimeUnit.SECONDS)) {
                log.info("userId:{},lock 획득 실패", req.getUserId());
                return false;
            }

            ResponseEntity<Boolean> response = itemServiceClient.reserveStock(req);
            reserveStock = response.getBody();
            log.info("userId:{}\nitemId:{}\nstockCount:{}\n재고 예약 가능 여부:{}", req.getUserId(), req.getItemId(), req.getCount(), reserveStock);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //락 반환
            lock.unlock();
        }

        return reserveStock;
    }


    /**
     * 주문 생성 요청
     * return : 양수 -> orderId값
     * : -1 -> 주문 생성 실패 -> 재고 예약 취소
     */
    @Transactional
    public Long requestCreateOrder(EnterPayRequestDto payReq) {

        //주문 생성
        OrderRequestDto req = new OrderRequestDto(payReq.getItemId(), payReq.getUserId(), payReq.getCount());
        ResponseEntity<Long> response = null;
        try {
            log.info("requestCreateOrder()\nuserId:{}", payReq.getUserId());

            response = orderServiceClient.createOrder(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.getBody();
    }

    /**
     * 구매 가능한지 체크: 일반 상품 or 예약 상품이면서 예약시간이 지났는지 확인
     */
    public boolean canPurchaseItem(EnterPayRequestDto requestDto) {
        ResponseEntity<CheckReserveResponseDto> response = itemServiceClient.getItemTypeAndTime(requestDto.getItemId());
        if (response.getBody().getType().equals("reserve")) {
            if (response.getBody().getReserveTime().isAfter(LocalDateTime.now())) {
                return false;
            }
        }
        return true;
    }
}