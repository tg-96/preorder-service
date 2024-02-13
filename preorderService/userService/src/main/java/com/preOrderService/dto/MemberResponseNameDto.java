package com.preOrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.service.annotation.GetExchange;

@Getter
@Setter
@AllArgsConstructor
public class MemberResponseNameDto {
    private String name;
}
