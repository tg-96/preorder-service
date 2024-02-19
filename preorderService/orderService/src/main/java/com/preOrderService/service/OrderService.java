package com.preOrderService.service;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.entity.Order;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.OrderServiceException;
import com.preOrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public Order createOrder(OrderRequestDto req){
        Order order = Order.createOrder(req.getItemId(), req.getUserId(), req.getQuantity(), req.getPrice());
        Order save = orderRepository.save(order);
        if (save == null) {
            throw new OrderServiceException(ErrorCode.CREATE_ORDER_ERROR);
        }
        return save;
    }

    /**
     * 주문 삭제
     */
    @Transactional
    public void deleteOrder(Long orderId){
        orderRepository.deleteById(orderId);
    }

    /**
     * 주문 상태 변경
     */
    @Transactional
    public void changeOrderStatus(OrderStatusRequestDto req){
        Order order = orderRepository.findById(req.getOrderId()).orElseThrow(() -> new OrderServiceException(ErrorCode.NO_EXIST_ORDER_ID));
    }

}
