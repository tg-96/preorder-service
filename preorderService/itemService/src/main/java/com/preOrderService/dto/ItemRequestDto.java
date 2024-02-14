package com.preOrderService.dto;

import com.preOrderService.entity.ItemType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private String name;
    private String content;
    private int price;
    private int stock;
    private LocalDateTime reserveTime;
    private String type;
}
