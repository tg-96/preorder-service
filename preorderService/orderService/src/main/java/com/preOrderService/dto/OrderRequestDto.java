package com.preOrderService.dto;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    @NonNull
    private Long itemId;

    @NonNull
    private Long userId;

    @NonNull
    @Min(value = 1, message = "최소 1개 이상은 주문해야 합니다.")
    private Long quantity;
}
