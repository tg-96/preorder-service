package com.preOrderService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private Long itemId;

    private Long userId;

    private Long quantity;

    private Long price;

    @Enumerated
    private OrderStatus orderStatus;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Order(Long itemId, Long userId, Long quantity, Long price) {
        this.itemId = itemId;
        this.userId = userId;
        this.quantity = quantity;
        this.price = price;
        this.orderStatus = OrderStatus.PRODUCT_VIEW; // 초기 상태
    }

    static public Order createOrder(Long itemId, Long userId, Long quantity, Long price){
        return new Order(itemId,userId,quantity,price);
    }

    public void changeOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }
}
