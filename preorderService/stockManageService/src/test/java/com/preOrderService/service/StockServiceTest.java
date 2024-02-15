package com.preOrderService.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private StockService stockService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Nested
    @DisplayName("재고 예약")
    class ReserveStock{
        @Test
        @DisplayName("성공")
        void success(){
            stockService.reserveStock(1L,1L);
            String key = String.format("reserveStock:itemId:%d",1L);
            Boolean bit = redisTemplate.opsForValue().getBit(key, 1L);
            assertThat(bit).isTrue();
        }
    }
}