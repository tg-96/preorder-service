package com.preOrderService.api.internal;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class InternalOrderController {
    private final OrderService orderService;
    /**
     * 주문 생성
     */
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody @Validated OrderRequestDto req){
        orderService.createOrder(req);
        return ResponseEntity.ok().body("주문이 생성되었습니다.");
    }

    /**
     * 주문 삭제
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId")Long orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().body("orderId: "+orderId+"가 삭제 되었습니다.");
    }
}
