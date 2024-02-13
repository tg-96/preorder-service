package com.preOrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    @Nullable
    private String name;
    @Nullable
    private String image;
    @Nullable
    private String introduction;
}
