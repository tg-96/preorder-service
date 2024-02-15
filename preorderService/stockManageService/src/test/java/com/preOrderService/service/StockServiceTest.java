package com.preOrderService.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class StockServiceTest {
    @Autowired
    private StockService stockService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @AfterEach
    void set(){
        Set<String> keys = redisTemplate.keys("*");
        if(keys != null && !keys.isEmpty()){
            redisTemplate.delete(keys);
        }
    }
    @Nested
    @DisplayName("재고 예약")
    class ReserveStock{
        @Test
        @DisplayName("성공")
        void success(){
            //given
            String key = String.format("reserveStock:itemId:%d",1L);

            //when
            stockService.reserveStock(1L,1L);
            Boolean bit = redisTemplate.opsForValue().getBit(key, 1L);

            //then
            assertThat(bit).isTrue();

        }
    }
    @Nested
    @DisplayName("재고 예약 취소")
    class CancelReserveStock{
        @Test
        @DisplayName("성공")
        void success(){
            //given
            stockService.reserveStock(11L,11L);
            String key = String.format("reserveStock:itemId:%d",11L);
            Boolean bit = redisTemplate.opsForValue().getBit(key, 11L);
            assertThat(bit).isTrue();

            //when
            stockService.cancelReserveStock(11L,11L);

            //then
            Boolean bit_cancel = redisTemplate.opsForValue().getBit(key, 11L);
            assertThat(bit_cancel).isFalse();
        }
    }

}