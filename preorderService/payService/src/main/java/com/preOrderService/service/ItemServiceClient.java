package com.preOrderService.service;

import com.preOrderService.dto.CheckReserveResponseDto;
import com.preOrderService.dto.PayRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "item-service",url = "http://localhost:8084/items")
public interface ItemServiceClient {
    @PostMapping("/stock/reserve")
    ResponseEntity<Boolean> reserveStock(@RequestBody PayRequestDto req);
    @PostMapping("/stock/cancel")
    ResponseEntity<Void> cancelStock(@RequestBody PayRequestDto req);
    @GetMapping("/type/{itemId}")
    ResponseEntity<CheckReserveResponseDto> getItemTypeAndTime(@PathVariable("itemId")Long itemId);

}