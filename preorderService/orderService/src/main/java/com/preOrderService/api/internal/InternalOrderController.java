package com.preOrderService.api.internal;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.OrdersResponseDto;
import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.entity.Orders;
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
    public ResponseEntity<Long> createOrder(@RequestBody @Validated OrderRequestDto req){
        Orders order = orderService.createOrder(req);
        return ResponseEntity.ok().body(order.getId());
    }

    /**
     * 주문 삭제
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId")Long orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().body("orderId: "+orderId+"가 삭제 되었습니다.");
    }

    /**
     * 주문 정보 변경
     */
    @PostMapping("/changeStatus")
    public ResponseEntity<String> changeStatus(@RequestBody OrderStatusRequestDto req){
        orderService.changeOrderStatus(req);
        return ResponseEntity.ok().body("orderId: "+req.getOrderId()+"의 주문 상태가 "+req.getStatus()+"로 변경 되었습니다.");
    }

    /**
     * 주문 정보 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrdersResponseDto> getOrderInfo(@PathVariable("orderId") Long orderId){
        OrdersResponseDto orderInfo = orderService.getOrderInfo(orderId);
        return ResponseEntity.ok().body(orderInfo);
    }
}
