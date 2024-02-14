package com.preOrderService.config.jwt;

import com.preOrderService.config.CustomUserDetails;
import com.preOrderService.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        // 토큰이 존재 하는지, Bearer 토큰이 맞는지 확인
        if(authorization == null || !authorization.startsWith("Bearer")){
            filterChain.doFilter(request,response);

            return;
        }
        //Bearer 부분 제거, 순수 토큰만 획득
        String token = "";
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring("Bearer ".length()).trim();
            // 토큰 사용
        }

        //토큰 소멸 시간 검증
        if(jwtUtil.isExpired(token)){
            //다음 필터로 전달
            filterChain.doFilter(request,response);

            return;
        }
        //토큰에서 memberId, role 획득
        Long memberId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);


        //member를 생성하여 값 set
        Member member = Member.builder()
                .id(memberId)
                .password("temp")
                .build();
        member.changeRole(role);
        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        /**
         * test
         **/
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority next = iterator.next();
        String authority = next.getAuthority();
        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails, null, customUserDetails.getAuthorities()
                );

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
