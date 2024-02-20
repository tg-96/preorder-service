package com.preOrderService.service;

import com.preOrderService.dto.ItemRequestDto;
import com.preOrderService.dto.StockRequest;
import com.preOrderService.entity.Item;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class StockServiceTest {
    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @AfterEach
    void tearDown(){
        //모든 키 삭제
        Set<String> keys = redisTemplate.keys("*");
        for(String key:keys){
            redisTemplate.delete(key);
        }
    }
    @Nested
    @DisplayName("재고 조회")
    class getStock{
        @Test
        @DisplayName("성공: 캐시에 재고가 있는 상태")
        void stockInCache() {
            //given
            Long itemId = 1L;
            String key = "item:stock "+itemId;
            redisTemplate.opsForValue().set(key,10L);

            //when
            Long stock = stockService.getStockByItemId(itemId);

            //then
            assertThat(stock).isEqualTo(10L);
        }
        @Test
        @DisplayName("성공: 캐시에 재고가 없는 상태")
        void stockNotInCache(){
            //given
            ItemRequestDto req = new ItemRequestDto("냉장고", "좋은 냉장고", 10000L, 10L, LocalDateTime.now().plusWeeks(1L), "reserve");
            Long itemId = productService.createItem(req).getId();

            //when
            Long stock = stockService.getStockByItemId(itemId);
            Long realTimeStock = redisTemplate.opsForValue().get("item:stock " + itemId);

            //then
            assertThat(stock).isEqualTo(10L);
            assertThat(realTimeStock).isEqualTo(10L);
        }
    }

    @Nested
    @DisplayName("재고 추가")
    class addStock{
        @Test
        @DisplayName("성공")
        void success(){
            //given
            ItemRequestDto itemRequestDto = new ItemRequestDto("냉장고", "좋은 냉장고", 10000L, 10L, LocalDateTime.now().plusWeeks(1L), "reserve");
            Long itemId = productService.createItem(itemRequestDto).getId();

            // 캐시에 재고 저장
            stockService.getStockByItemId(itemId);

            StockRequest req = new StockRequest(itemId,2L);
            String key = "item:stock "+itemId;

            //when
            stockService.addStock(req);

            //then
            Long realTimeStock = redisTemplate.opsForValue().get(key);
            Long stock = stockService.getStockByItemId(itemId);
            assertThat(realTimeStock).isEqualTo(12L);
            assertThat(stock).isEqualTo(12L);
        }

        @Test
        @DisplayName("캐시에 상품 재고가 올라가 있지 않다.")
        void StockNotInCache(){
            //given
            ItemRequestDto itemRequestDto = new ItemRequestDto("냉장고", "좋은 냉장고", 10000L, 10L, LocalDateTime.now().plusWeeks(1L), "reserve");
            Long itemId = productService.createItem(itemRequestDto).getId();
            StockRequest req = new StockRequest(itemId,2L);

            //when
            ItemServiceException ex = assertThrows(ItemServiceException.class, () -> {
                stockService.addStock(req);
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.STOCK_NOT_IN_CACHE);
        }

        @Test
        @DisplayName("증가시킬 재고 값이 0이하 이다.")
        void ReduceStockZeroError(){
            //given
            ItemRequestDto itemRequestDto = new ItemRequestDto("냉장고", "좋은 냉장고", 10000L, 10L, LocalDateTime.now().plusWeeks(1L), "reserve");
            Long itemId = productService.createItem(itemRequestDto).getId();
            StockRequest req = new StockRequest(itemId,0L);

            //when
            ItemServiceException ex = assertThrows(ItemServiceException.class, () -> {
                stockService.addStock(req);
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ADD_STOCK_ZERO_ERROR);
        }
    }
    @Nested
    @DisplayName("재고 감소")
    class ReduceStock{
        @Test
        @DisplayName("성공")
        void success(){
            //given
            ItemRequestDto itemRequestDto = new ItemRequestDto("냉장고", "좋은 냉장고", 10000L, 10L, LocalDateTime.now().plusWeeks(1L), "reserve");

            Long itemId = productService.createItem(itemRequestDto).getId();
            // 캐시에 재고 저장
            stockService.getStockByItemId(itemId);

            StockRequest req = new StockRequest(itemId,2L);
            String key = "item:stock "+itemId;

            //when
            stockService.reduceStock(req);

            //then
            Long realTimeStock = redisTemplate.opsForValue().get(key);
            Long stock = stockService.getStockByItemId(itemId);
            assertThat(realTimeStock).isEqualTo(8L);
            assertThat(stock).isEqualTo(8L);
        }
    }
}