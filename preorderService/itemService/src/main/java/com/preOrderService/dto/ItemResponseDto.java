package com.preOrderService.dto;

import com.preOrderService.entity.ItemType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ItemResponseDto {
    private Long id;
    private String name;
    private String content;
    private int price;
    private int stock;
    private LocalDateTime reserveTime;
    private ItemType type;
    @Builder
    public ItemResponseDto(Long id, String name, String content, int price, int stock, LocalDateTime reserveTime, ItemType type) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.price = price;
        this.stock = stock;
        this.reserveTime = reserveTime;
        this.type = type;
    }
}
