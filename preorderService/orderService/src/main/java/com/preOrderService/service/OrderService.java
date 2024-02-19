package com.preOrderService.service;

import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.OrderStatusRequestDto;
import com.preOrderService.entity.Orders;
import com.preOrderService.entity.OrderStatus;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.OrderServiceException;
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
    public Orders createOrder(OrderRequestDto req){
        Orders order = Orders.createOrder(req.getItemId(), req.getUserId(), req.getQuantity(), req.getPrice());
        Orders save = orderRepository.save(order);
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

        Orders order = orderRepository.findById(req.getOrderId()).orElseThrow(() -> new OrderServiceException(ErrorCode.NO_EXIST_ORDER_ID));

        if(req.getStatus().equalsIgnoreCase("PRODUCT_VIEW")){
            order.changeOrderStatus(OrderStatus.PRODUCT_VIEW);
        }
        else if(req.getStatus().equalsIgnoreCase("PAYMENT_VIEW")){
            order.changeOrderStatus(OrderStatus.PAYMENT_VIEW);
        }
        else if(req.getStatus().equalsIgnoreCase("PAYMENT_IN_PROGRESS")){
            order.changeOrderStatus(OrderStatus.PAYMENT_IN_PROGRESS);
        }
        else if(req.getStatus().equalsIgnoreCase("PAYMENT_COMPLETED")){
            order.changeOrderStatus(OrderStatus.PAYMENT_COMPLETED);
        }
        else{
            throw new OrderServiceException(ErrorCode.ORDER_STATUS_ERROR);
        }
    }

}
