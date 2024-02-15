package com.preOrderService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 재고 예약
     */
    @Transactional
    public void reserveStock(Long itemId,Long userId){
        String key = String.format("reserveStock:itemId:%d", itemId);
        redisTemplate.opsForValue().setBit(key,userId,true);
    }

    /**
     * 재고 예약 취소
     */
    @Transactional
    public void cancelReserveStock(Long itemId,Long userId){
        String key = String.format("reserveStock:itemId:%d", itemId);
        redisTemplate.opsForValue().setBit(key,userId,false);
    }

}
