//package com.preOrderService.service;
//
//import com.preOrderService.dto.JoinDto;
//import com.preOrderService.entity.Member;
//import com.preOrderService.repository.MemberRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//@SpringBootTest
//@Transactional
//@ActiveProfiles("test")
//class MemberServiceTest {
//    @Autowired
//    MemberService memberService;
//    @Autowired
//    MemberRepository memberRepository;
//    @Test
//    public void 회원가입(){
//        //given
//        JoinDto joinDto = new JoinDto();
//        joinDto.setEmail("aaa@bbb");ㅌ
//        joinDto.setName("진구");
//        joinDto.setRole("ADMIN");
//        joinDto.setPassword("123");
//
//        //when
//        memberService.join(joinDto);
//        //then
//        Member member = memberRepository.findByEmail("aaa@bbb").get();
//        assertThat(member.getEmail()).isEqualTo("aaa@bbb");
//    }
//
//    @Test
//    public void 중복회원가입(){
//        //given
//        JoinDto joinDto1 = new JoinDto();
//        joinDto1.setEmail("aaa@bbb");
//        joinDto1.setName("진구");
//        joinDto1.setRole("ADMIN");
//        joinDto1.setPassword("123");
//
//        JoinDto joinDto2 = new JoinDto();
//        joinDto2.setEmail("aaa@bbb");
//        joinDto2.setName("진구");
//        joinDto2.setRole("ADMIN");
//        joinDto2.setPassword("123");
//
//        //when
//        RuntimeException exception = assertThrows(RuntimeException.class,() ->{
//            memberService.join(joinDto1);
//            memberService.join(joinDto2);
//        });
//
//        //then
//        assertThat(exception.getMessage()).isEqualTo("가입 오류: 이미 가입한 적이 있는 이메일 입니다.");
//    }
//
//}