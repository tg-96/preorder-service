package com.preOrderService.api.internal;

import com.preOrderService.dto.MemberResponseNameDto;
import com.preOrderService.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class InternalMemberController {
    private final MemberService memberService;

    /**
     * memberId로 이름 조회
     */
    @GetMapping
    public String getMemberNameById(@RequestParam("id") Long memberId) {
        System.out.println("memberId = " + memberId);
        String name = memberService.findMemberNameById(memberId);

        return name;
    }

}
