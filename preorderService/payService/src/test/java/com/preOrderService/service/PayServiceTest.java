package com.preOrderService.service;


import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.dto.OrdersResponseDto;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
class PayServiceTest {
    @Autowired
    private PayService payService;

    @MockBean
    private OrderServiceClient orderServiceClient;

    @MockBean
    private ItemServiceClient itemServiceClient;

    @Nested
    @DisplayName("주문 상태 'PAYMENT_VIEW'인지 확인")
    class IsPaymentView {
        @Test
        @DisplayName("성공")
        void success() {
            //given
            OrdersResponseDto ordersResponseDto = new OrdersResponseDto(
                    1L, 1L, 1L, 2L, 2000L, "PAYMENT_VIEW", LocalDateTime.now(), LocalDateTime.now()
            );
            when(orderServiceClient.getOrderInfo(any(Long.class)))
                    .thenReturn(ResponseEntity.ok().body(ordersResponseDto));

            //when
            boolean isTrue = payService.isPaymentView(1L);

            //then
            assertThat(isTrue).isTrue();
        }

        @Test
        @DisplayName("실패: 주문 상태가 'PAYMENT_VIEW'가 아닌 경우")
        void fail() {
            //given
            OrdersResponseDto ordersResponseDto = new OrdersResponseDto(
                    1L, 1L, 1L, 2L, 2000L, "PAYMENT_IN_PROGRESS", LocalDateTime.now(), LocalDateTime.now()
            );
            when(orderServiceClient.getOrderInfo(any(Long.class)))
                    .thenReturn(ResponseEntity.ok().body(ordersResponseDto));

            when(orderServiceClient.changeStatus(any(OrderStatusRequestDto.class)))
                    .thenReturn(ResponseEntity.ok().build());

            when(itemServiceClient.reserveStock(any(EnterPayRequestDto.class)))
                    .thenReturn(ResponseEntity.ok().build());

            //when
            boolean isTrue = payService.isPaymentView(1L);

            //then
            assertThat(isTrue).isFalse();
        }

        @DisplayName("주문 상태 조회 API 에러")
        @Test
        void GET_ORDER_API_ERROR() {
            //given
            doThrow(new RuntimeException()).when(orderServiceClient).getOrderInfo(any(Long.class));

            //when
            PayServiceException ex = assertThrows(PayServiceException.class, () -> {
                payService.isPaymentView(1L);
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.GET_ORDER_API_ERROR);
        }
    }

    @Nested
    @DisplayName("주문 상태 변경 요청")
    class ChangeOrderStatus {
        @Test
        @DisplayName("성공")
        void success() {
            //given
            when(orderServiceClient.changeStatus(any(OrderStatusRequestDto.class)))
                    .thenReturn(ResponseEntity.ok().build());

            //when
            payService.changeOrderStatus(1L, "PAYMENT_COMPLETED");

            //then
            verify(orderServiceClient, times(1)).changeStatus(any(OrderStatusRequestDto.class));
        }

        @Test
        @DisplayName("주문 상태 변경 API 에러")
        void ChangeOrderStatusApiError() {
            //given
            doThrow(new RuntimeException()).when(orderServiceClient).changeStatus(any(OrderStatusRequestDto.class));

            //when
            PayServiceException ex = assertThrows(PayServiceException.class, () -> {
                payService.changeOrderStatus(1L,"COMPLETE");
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CHANGE_ORDER_STATUS_API_ERROR);
        }
    }
}