package com.preOrderService.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String name;
    private String email;
    private String image;
    private String introduction;
}
