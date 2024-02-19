package com.preOrderService.dto;

import com.preOrderService.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersResponseDto {
    private Long orderId;

    private Long itemId;

    private Long userId;

    private Long quantity;

    private Long price;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
