package com.preOrderService.api.internal;

import com.preOrderService.service.StockService;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
public class StockManageController {

    private final StockService stockService;

    /**
     * 재고 예약
     */
    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@RequestParam Long itemId, @RequestParam Long userId){
        stockService.reserveStock(itemId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 재고 예약 취소
     */
    @PostMapping("/reserve/cancel")
    public ResponseEntity<Void> reserveStockCancel(@RequestParam Long itemId, @RequestParam Long userId){
        stockService.cancelReserveStock(itemId, userId);
        return ResponseEntity.ok().build();
    }




}
