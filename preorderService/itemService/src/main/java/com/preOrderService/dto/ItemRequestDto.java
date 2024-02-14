package com.preOrderService.dto;

import com.preOrderService.entity.ItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDto {

    private String name;
    private String content;
    private int price;
    private int stock;
    private LocalDateTime reserveTime;
    private String type;
}
