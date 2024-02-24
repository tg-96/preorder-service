package com.preOrderService.api.external;

import com.preOrderService.dto.OrderIdRequestDto;
import com.preOrderService.dto.PayRequestDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import com.preOrderService.service.EnterPayService;
import com.preOrderService.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@Slf4j
public class ExternalPayController {
    private final EnterPayService enterPayService;
    private final PayService payService;
    private final RedissonClient redissonClient;


    /**
     * 결제 진입 API
     * return: orderId
     */
    @PostMapping("/enter")
    public ResponseEntity<Long> enterPay(@RequestBody PayRequestDto req) throws InterruptedException {
        log.info("userId:{} 결제 진입", req.getUserId());

        //구매 가능한지 체크
        if (!enterPayService.canPurchaseItem(req)) {
            log.info("userId:{} 구매 불가능", req.getUserId());

            throw new PayServiceException(ErrorCode.NOT_AVAILABLE_TIME_TO_PURCHASE);
        }
        log.info("userId:{} 구매 가능", req.getUserId());

        //Lock
        String lockKey = "lockKey:" + req.getItemId();
        RLock lock = redissonClient.getLock(lockKey);

        //락 획득
        boolean success = lock.tryLock(10, 1, TimeUnit.SECONDS);

        if(!success){
            log.info("lock 획득 실패");
            return ResponseEntity.ok().body(-1L);
        }

        //재고 예약
        log.info("userId:{} 재고 예약 요청", req.getUserId());
        boolean reserveStock = enterPayService.reserveStockRequest(req);

        if(reserveStock){
            log.info("userId:{}, 재고 예약 성공",req.getUserId());
        }else{
            log.info("userId:{}, 재고 예약 실패",req.getUserId());
            throw new PayServiceException(ErrorCode.OUT_OF_STOCK);
        }

        //락 반환
        lock.unlock();

        //주문 생성 요청, 실패 했을 경우 -1 리턴
        Long orderId = enterPayService.requestCreateOrder(req);

        return ResponseEntity.ok().body(orderId);
    }

    /**
     * 결제 API
     */
    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody OrderIdRequestDto req) {
        log.info("orderId:{},결제 API",req.getOrderId());
        //주문 실패
        if(req.getOrderId() == -1L){
            return ResponseEntity.ok().body("주문 생성을 실패했습니다.");
        }

        //주문 상태 'PAYMENT_VIEW'인지 확인
        if (!payService.isPaymentView(req.getOrderId())) {
            log.info("orderId:{}, paymentView 상태가 아니라 주문 취소");
            return ResponseEntity.ok().body("정상적인 주문 상태가 아닙니다.");
        }

        //'PAYMENT_IN_PROGRESS'으로 주문 상태 변환
        log.info("orderId:{},주문 상태 'PAYMENT_IN_PROGRESS'로 변경",req.getOrderId());
        payService.changeOrderStatus(req.getOrderId(), "PAYMENT_IN_PROGRESS");

        //20% 결제 실패 (잔액 부족 등 여러 상황 시뮬)
        double probability = Math.random();
        if (probability <= 0.2) { // 20% 확률로 조건 충족
            //주문 취소
            log.info("orderId:{},결제 중 실패(잔액 부족)",req.getOrderId());

            payService.cancelOrder(req.getOrderId());
            return ResponseEntity.ok().body("카드사 요청에 의해 주문이 취소되었습니다.");
        }

        //'PAYMENT_COMPLETED'로 주문 상태 변환
        log.info("orderId:{},주문 상태 'PAYMENT_COMPLETED'로 변경",req.getOrderId());

        payService.changeOrderStatus(req.getOrderId(), "PAYMENT_COMPLETED");

        return ResponseEntity.ok().body("결제가 완료되었습니다.");
    }

    /**
     * 예약 시간 동시주문 상황 시뮬레이션
     */
    @PostMapping("/test")
    public ResponseEntity<String> simulate(@RequestBody PayRequestDto req) throws InterruptedException {
        //결제 진입
        ResponseEntity<Long> enterPayResponse = enterPay(req);

        log.info("userId:{} / 결제 진입 후 생성된 orderId: {}", req.getUserId(), enterPayResponse.getBody());

        //생성된 주문 Id
        Long orderId = enterPayResponse.getBody();

        //20% 주문 실패 (고객 변심으로 주문 취소)
        double probability = Math.random();
        if (probability <= 0.2) { // 20% 확률로 조건 충족
            //주문 취소
            log.info("userId:{}, orderId:{} / 주문 취소", req.getUserId(), orderId);

            payService.cancelOrder(orderId);
            return ResponseEntity.ok().body("고객 변심으로 주문이 취소되었습니다.");
        }

        //결제
        log.info("userId:{}, orderId:{} 결제", req.getUserId(), orderId);

        OrderIdRequestDto orderIdRequestDto = new OrderIdRequestDto(orderId);
        ResponseEntity<String> payResponse = pay(orderIdRequestDto);

        log.info("userId:{}, orderId:{},{}", req.getUserId(), orderId,payResponse);

        return payResponse;
    }

}