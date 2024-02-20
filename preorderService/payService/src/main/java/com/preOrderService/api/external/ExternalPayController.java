package com.preOrderService.api.external;

import com.preOrderService.dto.PayRequestDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import com.preOrderService.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class ExternalPayController {
    private final PayService payService;

    /**
     * 결제 진입 API
     */
    @PostMapping("/start")
    public ResponseEntity<String> startPay(@RequestBody PayRequestDto req) {
        //구매 가능한지 체크
        if (!payService.canPurchaseItem(req)) {
            throw new PayServiceException(ErrorCode.NOT_AVAILABLE_TIME_TO_PURCHASE);
        }

        //재고가 남았는지 체크
        if(!payService.isRemainStock(req)){
            throw new PayServiceException(ErrorCode.OUT_OF_STOCK);
        }

        //재고 차감 요청
        payService.requestReduceStock(req);

        //주문 생성 요청
        payService.requestCreateOrder(req);

        return ResponseEntity.ok().body("결제 진입이 허용되었습니다.");
    }
}
