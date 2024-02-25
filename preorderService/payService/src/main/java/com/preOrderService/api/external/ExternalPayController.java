package com.preOrderService.api.external;

import com.preOrderService.dto.EnterPayRequestDto;
import com.preOrderService.dto.PayRequestDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import com.preOrderService.service.EnterPayService;
import com.preOrderService.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@Slf4j
public class ExternalPayController {
    private final EnterPayService enterPayService;
    private final PayService payService;


    /**
     * 결제 진입 API
     * return: orderId
     */
    @PostMapping("/enter")
    public ResponseEntity<PayRequestDto> enterPay(@RequestBody EnterPayRequestDto req) {
        log.info("userId:{}, 결제 진입", req.getUserId());

        //구매 가능한지 체크
        if (!enterPayService.canPurchaseItem(req)) {
            log.info("userId:{} 구매 불가능", req.getUserId());

            throw new PayServiceException(ErrorCode.NOT_AVAILABLE_TIME_TO_PURCHASE);
        }
        log.info("userId:{} 구매 가능", req.getUserId());

        //재고 예약
        log.info("userId:{} 재고 예약 요청", req.getUserId());
        boolean reserveStock = enterPayService.reserveStockRequest(req);

        if (reserveStock) {
            log.info("userId:{}, 재고 예약 성공", req.getUserId());
        } else {
            log.info("userId:{}, 재고 예약 실패", req.getUserId());
            throw new PayServiceException(ErrorCode.OUT_OF_STOCK);
        }

        //주문 생성 요청
        Long orderId = enterPayService.requestCreateOrder(req);
        PayRequestDto payRequestDto = new PayRequestDto(req, orderId);

        return ResponseEntity.ok().body(payRequestDto);
    }

    /**
     * 결제 API
     */
    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody PayRequestDto req) {
        log.info("orderId:{},결제 API", req.getOrderId());

        //주문 상태 'PAYMENT_VIEW'인지 확인
        if (!payService.isPaymentView(req)) {
            log.info("orderId:{}, paymentView 상태가 아니라 주문 취소");
            throw new PayServiceException(ErrorCode.IS_NOT_PAYMENT_VIEW_STATUS);
        }

        //'PAYMENT_IN_PROGRESS'으로 주문 상태 변환
        log.info("주문 상태 'PAYMENT_IN_PROGRESS'로 변경\norderId:{},userId:{}", req.getOrderId(), req.getUserId());
        payService.changeOrderStatus(req.getOrderId(), "PAYMENT_IN_PROGRESS");

        //20% 결제 실패 (잔액 부족 등 여러 상황 시뮬)
        double probability = Math.random();
        if (probability <= 0.2) { // 20% 확률로 조건 충족
            //주문 취소
            log.info("결제 중 실패(잔액 부족)\nuserId:{}\norderId:{}", req.getUserId(), req.getOrderId());

            payService.cancelOrder(req);
            throw new PayServiceException(ErrorCode.FAIL_TO_PAY_BECAUSE_OUT_OF_BALANCE);
        }

        //'PAYMENT_COMPLETED'로 주문 상태 변환
        log.info("주문 상태 'PAYMENT_COMPLETED'로 변경\nuserId:{}\norderId:{}", req.getUserId(), req.getOrderId());

        payService.changeOrderStatus(req.getOrderId(), "PAYMENT_COMPLETED");

        return ResponseEntity.ok().body("complete pay");
    }

    /**
     * 예약 시간 10000명 동시주문 상황 시뮬레이션
     */
    @PostMapping("/test")
    public ResponseEntity<String> simulate(@RequestBody EnterPayRequestDto req) {
        //결제 진입
        ResponseEntity<PayRequestDto> enterPayResponse = null;
        try {
            log.info("결제 진입 시작\nuserId:{}", req.getUserId());
            enterPayResponse = enterPay(req);
            log.info("결제 진입 완료\nuserId:{}\norderId:{}", enterPayResponse.getBody().getUserId(), enterPayResponse.getBody().getOrderId());

        } catch (PayServiceException ex) {
            log.info("결제 진입 실패\nuserId:{}", req.getUserId());
            return ResponseEntity.ok().body("can't continue pay");
        }

        log.info("userId:{} / 결제 진입 후 생성된 dto: {}", req.getUserId(), enterPayResponse.getBody());

        //생성된 주문 Id
        Long orderId = enterPayResponse.getBody().getOrderId();

        //20% 주문 실패 (고객 변심으로 주문 취소)
        double probability = Math.random();
        if (probability <= 0.2) { // 20% 확률로 조건 충족
            //주문 취소
            log.info("userId:{}, orderId:{} / 주문 취소", req.getUserId(), orderId);

            payService.cancelOrder(enterPayResponse.getBody());
            return ResponseEntity.ok().body("fail to pay because customer cancel");
        }

        //결제
        log.info("userId:{}, orderId:{} 결제 시작", req.getUserId(), orderId);
        ResponseEntity<String> payResponse = null;
        try {
            payResponse = pay(enterPayResponse.getBody());
        }catch (PayServiceException ex){
            return ResponseEntity.ok().body(ex.getErrorCode().toString());
        }
        log.info("userId:{}, orderId:{},{}", req.getUserId(), orderId, payResponse);

        return payResponse;
    }

}