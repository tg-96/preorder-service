package com.preOrderService.service;

import com.preOrderService.dto.MemberResponseDto;
import com.preOrderService.config.jwt.JWTUtil;
import com.preOrderService.entity.Member;
import com.preOrderService.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalMemberService {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    JWTUtil jwtUtil;

    /**
     * memberId로 멤버 조회
     */
    public MemberResponseDto getMemberById(String token, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버 정보가 없습니다."));

        return new MemberResponseDto(
                member.getId(), member.getEmail(), member.getPassword(), member.getImage(), member.getIntroduction()
        );
    }
}
