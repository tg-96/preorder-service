package com.preOrderService.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class StockServiceTest {

    private MockWebServer mockWebServer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private StockService stockService;

    @AfterEach
    void set() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Nested
    @DisplayName("재고 예약")
    class ReserveStock {
        @Test
        @DisplayName("성공")
        void success() {
            //given
            String key = String.format("reserveStock:itemId:%d", 1L);

            //when
            stockService.reserveStock(1L, 1L);
            Boolean bit = redisTemplate.opsForValue().getBit(key, 1L);

            //then
            assertThat(bit).isTrue();

        }
    }

    @Nested
    @DisplayName("재고 예약 취소")
    class CancelReserveStock {
        @Test
        @DisplayName("성공")
        void success() {
            //given
            stockService.reserveStock(11L, 11L);
            String key = String.format("reserveStock:itemId:%d", 11L);
            Boolean bit = redisTemplate.opsForValue().getBit(key, 11L);
            assertThat(bit).isTrue();

            //when
            stockService.cancelReserveStock(11L, 11L);

            //then
            Boolean bit_cancel = redisTemplate.opsForValue().getBit(key, 11L);
            assertThat(bit_cancel).isFalse();
        }
    }

    @Nested
    @DisplayName("예약 가능한 재고 수 조회")
    class availableReserveStock {
        @BeforeEach
        void setUp() throws IOException {
            mockWebServer = new MockWebServer();
            mockWebServer.start();

            String baseUrl = mockWebServer.url("/").toString();
            WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
            ReflectionTestUtils.setField(stockService,"itemServiceClient",webClient);

        }
        @AfterEach
        void tearDown() throws IOException{
            mockWebServer.shutdown();
        }

        @Test
        @DisplayName("예약된 재고 수 카운트")
        void countReserveStock() {
            //given
            stockService.reserveStock(11L, 1L);
            stockService.reserveStock(11L, 2L);
            stockService.reserveStock(11L, 3L);

            //when
            Long count = stockService.countReserveStock(11L);

            //then
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("예약된 재고 조회 안됨.")
        void NotExistReserveStock() {
            //when
            Long count = stockService.countReserveStock(11L);

            //then
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("성공")
        void success() {
            //given
            Long itemId = 11L;
            stockService.reserveStock(itemId, 1L);
            stockService.reserveStock(itemId, 2L);
            stockService.reserveStock(itemId, 3L);
            mockWebServer.enqueue(new MockResponse().setBody("10").setResponseCode(200));

            //when
            Mono<Long> availableReserveStock = stockService.getAvailableReserveStock(itemId);

            //then
            availableReserveStock.subscribe(stock -> assertThat(stock).isEqualTo(7));
        }
    }

}