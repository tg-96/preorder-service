package com.preOrderService.service;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.entity.Order;
import com.preOrderService.entity.OrderStatus;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.OrderServiceException;
import com.preOrderService.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("주문 생성")
    class createOrder {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            OrderRequestDto req = new OrderRequestDto(1L, 1L, 1L, 1000L);
            Mockito.when(orderRepository.save(Mockito.any())).
                    thenAnswer(invocation -> invocation.getArgument(0));
            //when
            Order order = orderService.createOrder(req);

            //then
            Assertions.assertThat(order.getItemId()).isEqualTo(1L);
            Assertions.assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PRODUCT_VIEW);
        }

        @Test
        @DisplayName("저장이 되지 않았을 경우")
        void CREATE_ORDER_ERROR(){
            //given
            OrderRequestDto req = new OrderRequestDto(1L, 1L, 1L, 1000L);
            Mockito.when(orderRepository.save(Mockito.any())).
                    thenAnswer(invocation -> null);

            //when
            OrderServiceException ex = assertThrows(OrderServiceException.class,() -> {
                orderService.createOrder(req);
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CREATE_ORDER_ERROR);
        }
    }
}