package com.preOrderService.dto;

import com.preOrderService.entity.ItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private String name;

    private String content;

    @Min(value = 0,message = "최소 0원 이상이어야 합니다.")
    private Long price;

    @Min(value = 0,message = "최소 0개 이상이어야 합니다.")
    private Long stock;

    private LocalDateTime reserveTime;

    @Pattern(regexp = "^(general|reserve)$", message = "'reserve'혹은'general' 타입만 가능합니다.")
    private String type;
}
