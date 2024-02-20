package com.preOrderService.api.external;

import com.preOrderService.dto.PayRequestDto;
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
        payService.checkReserveTime(req);
        return ResponseEntity.ok().body("결제 진입이 허용되었습니다.");
    }
}
