package com.preOrderService.service;

import com.preOrderService.dto.CheckReserveResponseDto;
import com.preOrderService.dto.StockRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "item-service",url = "http://localhost:8084/items")
@Qualifier("item-service")
public interface ItemServiceClient {
    @GetMapping("/stock/{itemId}")
    ResponseEntity<Long> getStockByItemId(@PathVariable("itemId")Long itemId);
    @GetMapping("/type/{itemId}")
    ResponseEntity<CheckReserveResponseDto> getItemTypeAndTime(@PathVariable("itemId")Long itemId);
    @PatchMapping("/stock/add")
    ResponseEntity<Void> addStock(@RequestBody StockRequest req);
    @PatchMapping("/stock/reduce")
    ResponseEntity<Void> reduceStock(@RequestBody StockRequest req);
}
