package com.preOrderService.service;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.dto.OrdersResponseDto;
import com.preOrderService.entity.OrderStatus;
import com.preOrderService.entity.Orders;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceTest.class);

    @Nested
    @DisplayName("주문 생성")
    class createOrder {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            OrderRequestDto req = new OrderRequestDto(1L, 1L, 1L);
            Mockito.when(orderRepository.save(Mockito.any())).
                    thenAnswer(invocation -> invocation.getArgument(0));
            //when
            Orders order = orderService.createOrder(req);

            //then
            Assertions.assertThat(order.getItemId()).isEqualTo(1L);
            Assertions.assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_VIEW);
        }

        @Test
        @DisplayName("저장이 되지 않았을 경우")
        void CREATE_ORDER_ERROR() {
            //given
            OrderRequestDto req = new OrderRequestDto(1L, 1L, 1L);
            Mockito.when(orderRepository.save(Mockito.any())).
                    thenAnswer(invocation -> null);

            //when
            OrderServiceException ex = assertThrows(OrderServiceException.class, () -> {
                orderService.createOrder(req);
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CREATE_ORDER_ERROR);
        }
    }

    @Nested
    @DisplayName("주문 정보 변경")
    @SpringBootTest
    class ChangeOrderStatus {

        @Autowired
        OrderRepository orderRepository;

        @Autowired
        OrderService orderService;

        @DisplayName("성공")
        @Test
        @Transactional
        void success() {
            //given
            Orders order = Orders.createOrder(1L, 1L, 10L);
            Orders save = orderRepository.saveAndFlush(order);
            OrderStatusRequestDto req = new OrderStatusRequestDto(save.getId(), "PAYMENT_IN_PROGRESS");
            logger.info("req.orderId: {}", req.getOrderId());
            logger.info("orderId: {}", save.getId());

            //when
            orderService.changeOrderStatus(req);

            //then
            assertThat(save.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_VIEW);
        }

        @DisplayName("OrderStatusRequestDto의 orderStatus가 정상적이지 않다.")
        @Test
        @Transactional
        void orderStatusError() {
            //given
            Orders order = Orders.createOrder(1L, 1L, 10L);
            Orders save = orderRepository.saveAndFlush(order);
            OrderStatusRequestDto req = new OrderStatusRequestDto(save.getId(), "aaa");

            //when
            OrderServiceException ex = assertThrows(OrderServiceException.class, () -> {
                orderService.changeOrderStatus(req);
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ORDER_STATUS_ERROR);
        }
    }

    @Nested
    @DisplayName("주문 정보 조회")
    @SpringBootTest
    @ActiveProfiles("test")
    @Transactional
    class GetOrderInfo {
        @Autowired
        OrderRepository orderRepository;

        @Autowired
        OrderService orderService;
        @Test
        @DisplayName("성공")
        void success() {
            //given
            OrderRequestDto req = new OrderRequestDto(1L, 1L, 100L);
            Orders order = orderService.createOrder(req);
            Long id = order.getId();

            //when
            OrdersResponseDto response = orderService.getOrderInfo(id);

            //then
            assertThat(response.getItemId()).isEqualTo(1L);
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getQuantity()).isEqualTo(100L);
        }
        @Test
        @DisplayName("주문 조회 안됨.")
        void NO_EXIST_ORDER_ID(){
            //when
            OrderServiceException ex = assertThrows(OrderServiceException.class,()->{
                orderService.getOrderInfo(1L);
            });

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NO_EXIST_ORDER_ID);
        }
    }
}