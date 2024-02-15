package com.preOrderService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {
    private final RedisTemplate<String, String> redisTemplate;

    @Qualifier("ItemServiceClient")
    private final WebClient itemServiceClient;

    /**
     * 재고 예약
     */
    @Transactional
    public void reserveStock(Long itemId, Long userId) {
        String key = String.format("reserveStock:itemId:%d", itemId);
        redisTemplate.opsForValue().setBit(key, userId, true);
    }

    /**
     * 재고 예약 취소
     */
    @Transactional
    public void cancelReserveStock(Long itemId, Long userId) {
        String key = String.format("reserveStock:itemId:%d", itemId);
        redisTemplate.opsForValue().setBit(key, userId, false);
    }

    /**
     * 예약 가능한 재고 수 조회
     */
    public Mono<Long> getAvailableReserveStock(Long itemId) {
        //현재 재고 비동기적으로 가져옴.
        Mono<Long> currentStockMono = itemServiceClient.get()
                .uri("/items/stock/{itemId}", itemId)
                .retrieve()
                .bodyToMono(Long.class);
        //redis에서 예약 재고 계산
        Mono<Long> reservedStockMono = Mono.fromCallable(() -> countReserveStock(itemId));

        //예약가능한 재고 수 = 현재 재고 - 예약 재고
        return Mono.zip(currentStockMono, reservedStockMono, (currentStock, reserveStock) -> currentStock - reserveStock);
    }

    /**
     * 예약된 재고 수 카운트
     */
    public Long countReserveStock(Long itemId) {
        String key = String.format("reserveStock:itemId:%d", itemId);
        Long reservedStockCount = redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.bitCount(key.getBytes(StandardCharsets.UTF_8)));
        return reservedStockCount != null ? reservedStockCount : 0L;
    }
}
