package com.preOrderService.service;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.entity.Orders;
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
            OrderRequestDto req = new OrderRequestDto(1L, 1L, 1L, 1000L);
            Mockito.when(orderRepository.save(Mockito.any())).
                    thenAnswer(invocation -> invocation.getArgument(0));
            //when
            Orders order = orderService.createOrder(req);

            //then
            Assertions.assertThat(order.getItemId()).isEqualTo(1L);
            Assertions.assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PRODUCT_VIEW);
        }

        @Test
        @DisplayName("저장이 되지 않았을 경우")
        void CREATE_ORDER_ERROR() {
            //given
            OrderRequestDto req = new OrderRequestDto(1L, 1L, 1L, 1000L);
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
            Orders order = Orders.createOrder(1L, 1L, 10L, 1000L);
            Orders save = orderRepository.saveAndFlush(order);
            OrderStatusRequestDto req = new OrderStatusRequestDto(save.getId(), "payment_VIEW");
            logger.info("req.orderId: {}",req.getOrderId());
            logger.info("orderId: {}",save.getId());

            //when
            orderService.changeOrderStatus(req);

            //then
            assertThat(save.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_VIEW);
        }
        @DisplayName("OrderStatusRequestDto의 orderStatus가 정상적이지 않다.")
        @Test
        @Transactional
        void OrderStatusError(){
            //given
            Orders order = Orders.createOrder(1L, 1L, 10L, 1000L);
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
}