package com.preOrderService.service;

import com.preOrderService.dto.CheckReserveResponseDto;
import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.EnterPayRequestDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
class EnterPayServiceTest {
    @Autowired
    private EnterPayService payService;

    @MockBean
    private OrderServiceClient orderServiceClient;

    @MockBean
    private ItemServiceClient itemServiceClient;

    @Nested
    @DisplayName("주문 생성 요청")
    class RequestCreateOrder {
        @Test
        @DisplayName("success")
        void success() {
            //given
            EnterPayRequestDto payRequestDto = new EnterPayRequestDto(1L, 1L, 2L);

            //when
            payService.requestCreateOrder(payRequestDto);

            //then
            verify(orderServiceClient, times(1)).createOrder(any(OrderRequestDto.class));
            verify(itemServiceClient, never()).cancelStock(any(EnterPayRequestDto.class));
        }

        @Test
        @DisplayName("실패 시 재고 원상 복귀 요청")
        void fail() {
            //given
            EnterPayRequestDto payRequestDto = new EnterPayRequestDto(1L, 1L, 2L);
            doThrow(new RuntimeException()).when(orderServiceClient).createOrder(any(OrderRequestDto.class));

            //when
            PayServiceException ex = assertThrows(PayServiceException.class, () -> {
                payService.requestCreateOrder(payRequestDto);
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CREATE_ORDER_API_ERROR);
            verify(orderServiceClient, times(1)).createOrder(any(OrderRequestDto.class));
            verify(itemServiceClient, times(1)).cancelStock(any(EnterPayRequestDto.class));
        }
    }

    @Nested
    @DisplayName("구매 가능한지 체크")
    class CanPurchaseItem {
        @DisplayName("성공: 일반 상품일 경우")
        @Test
        void success() {
            //given
            EnterPayRequestDto payRequestDto = new EnterPayRequestDto(1L, 1L, 2L);
            CheckReserveResponseDto general = new CheckReserveResponseDto("general", null);
            when(itemServiceClient.getItemTypeAndTime(any(Long.class))).thenReturn(ResponseEntity.ok().body(general));

            //when
            boolean can = payService.canPurchaseItem(payRequestDto);

            //then
            verify(itemServiceClient, times(1)).getItemTypeAndTime(any(Long.class));
            assertThat(can).isTrue();
        }

        @DisplayName("성공: 예약 상품,예약 시간이 지난 경우")
        @Test
        void success2() {
            //given
            EnterPayRequestDto payRequestDto = new EnterPayRequestDto(1L, 1L, 2L);
            CheckReserveResponseDto reserve = new CheckReserveResponseDto("reserve", LocalDateTime.now().minusDays(2L));
            when(itemServiceClient.getItemTypeAndTime(any(Long.class))).thenReturn(ResponseEntity.ok().body(reserve));

            //when
            boolean can = payService.canPurchaseItem(payRequestDto);

            //then
            verify(itemServiceClient, times(1)).getItemTypeAndTime(any(Long.class));
            assertThat(can).isTrue();
        }

        @DisplayName("실패: 예약 상품, 예약 시간이 아직 되지 않은 경우")
        @Test
        void fail() {
            //given
            EnterPayRequestDto payRequestDto = new EnterPayRequestDto(1L, 1L, 2L);
            CheckReserveResponseDto reserve = new CheckReserveResponseDto("reserve", LocalDateTime.now().plusDays(2L));
            when(itemServiceClient.getItemTypeAndTime(any(Long.class))).thenReturn(ResponseEntity.ok().body(reserve));

            //when
            boolean can = payService.canPurchaseItem(payRequestDto);

            //then
            verify(itemServiceClient, times(1)).getItemTypeAndTime(any(Long.class));
            assertThat(can).isFalse();
        }
    }

    @Nested
    @DisplayName("재고 예약")
    class IsRemainStock {
        @Test
        @DisplayName("성공")
        void success() {
            //given
            EnterPayRequestDto payRequestDto = new EnterPayRequestDto(1L, 1L, 2L);
            when(itemServiceClient.reserveStock(any(EnterPayRequestDto.class))).thenReturn(ResponseEntity.ok().body(true));

            //when
            boolean reserveStock = payService.reserveStockRequest(payRequestDto);

            //then
            assertThat(reserveStock).isTrue();
        }
        @Test
        @DisplayName("재고가 부족")
        void outOfStock() {
            //given
            EnterPayRequestDto payRequestDto = new EnterPayRequestDto(1L, 1L, 2L);
            when(itemServiceClient.reserveStock(any(EnterPayRequestDto.class))).thenReturn(ResponseEntity.ok().body(false));

            //when
            boolean reserveStock = payService.reserveStockRequest(payRequestDto);

            //then
            assertThat(reserveStock).isFalse();
        }

    }

}