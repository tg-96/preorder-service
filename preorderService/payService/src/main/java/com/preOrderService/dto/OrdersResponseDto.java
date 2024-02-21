package com.preOrderService.dto;

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

    private String orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
