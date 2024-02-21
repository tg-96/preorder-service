package com.preOrderService.service;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.dto.OrdersResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service",url="http://localhost:8083/order")
public interface OrderServiceClient {
    @PostMapping
    ResponseEntity<Long> createOrder(@RequestBody @Validated OrderRequestDto req);

    @GetMapping("/{orderId}")
    ResponseEntity<OrdersResponseDto> getOrderInfo(@PathVariable("orderId") Long orderId);

    @PostMapping("/changeStatus")
    ResponseEntity<String> changeStatus(@RequestBody OrderStatusRequestDto req);
}