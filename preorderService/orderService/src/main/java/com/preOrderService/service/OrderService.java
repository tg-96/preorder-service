package com.preOrderService.service;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.entity.Order;
import com.preOrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public void createOrder(OrderRequestDto req){
        Order order = Order.createOrder(req.getItemId(), req.getUserId(), req.getQuantity(), req.getPrice());
        orderRepository.save(order);
    }

}
