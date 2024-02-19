package com.preOrderService.service;

import com.preOrderService.dto.OrderRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service",url="http://localhost:8084/order")
public interface OrderServiceClient {
    @PostMapping
    ResponseEntity<Long> createOrder(@RequestBody @Validated OrderRequestDto req);
}
