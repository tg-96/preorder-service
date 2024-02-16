package com.preOrderService.dto;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDto {
    @NonNull
    private Long itemId;

    @NonNull
    private Long userId;

    @NonNull
    @Min(value = 1, message = "최소 1개 이상은 주문해야 합니다.")
    private Long quantity;

    @NonNull
    @Min(value = 0, message = "최소 0원 이상이어야 합니다.")
    private Long price;
}
