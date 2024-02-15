package com.preOrderService.api.internal;

import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.StockManageServiceException;
import com.preOrderService.service.StockService;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    /**
     * 예약 가능한 재고 수 조회
     */
    @GetMapping("/reserve/available/{itemId}")
    public Mono<ResponseEntity<Long>> getAvailableReserveStock(@PathVariable("itemId") Long itemId){
        Mono<Long> availableReserveStock = stockService.getAvailableReserveStock(itemId);
        return availableReserveStock
                .map(stock ->ResponseEntity.ok(stock))
                .switchIfEmpty(Mono.error(new StockManageServiceException(ErrorCode.NOT_FOUND_STOCK)));
    }




}
