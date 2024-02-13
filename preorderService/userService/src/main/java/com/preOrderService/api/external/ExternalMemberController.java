package com.preOrderService.api.external;

import com.preOrderService.config.jwt.JWTUtil;
import com.preOrderService.dto.JoinDto;
import com.preOrderService.dto.MemberDto;
import com.preOrderService.dto.MemberResponseDto;
import com.preOrderService.dto.PwdUpdateDto;
import com.preOrderService.service.AwsS3Service;
import com.preOrderService.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class ExternalMemberController {
    private final MemberService memberService;
    private final AwsS3Service awsS3Service;
    private final JWTUtil jwtUtil;

    /**
     * 프로필 조회
     */
    @GetMapping
    public MemberResponseDto findMember(@RequestHeader("Authorization") String token) {
        String parse_token = jwtUtil.parser(token);
        //토큰 유효기간 확인
        if (jwtUtil.isExpired(parse_token)) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }

        Long userId = jwtUtil.getUserId(parse_token);

        return memberService.getProfile(userId);
    }
    /**
     * 프로필 업데이트
     */
    @PatchMapping
    public ResponseEntity<Void> updateMember(@RequestPart(value = "memberDto") MemberDto memberDto,
                                             @RequestPart(value = "file") MultipartFile multipartFile,
                                             @RequestHeader("Authorization") String token) {
        String parse_token = jwtUtil.parser(token);

        //토큰 유효기간 확인
        if(jwtUtil.isExpired(parse_token)){
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }

        //이미지 정보 s3에 업로드후, 프로필 이미지 추가
        if (!multipartFile.isEmpty()) {
            String image = awsS3Service.uploadFile(multipartFile);
            memberDto.setImage(image);
        }

        Long userId = jwtUtil.getUserId(parse_token);
        memberService.updateProfile(memberDto, userId);

        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 업데이트
     **/
    @PutMapping("/pwd")
    public ResponseEntity<Void> updatePwd(@RequestHeader("Authorization") String token, @RequestBody PwdUpdateDto pwdUpdateDto) {
        String parse_token = jwtUtil.parser(token);

        if (jwtUtil.isExpired(parse_token)) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }

        Long memberId = jwtUtil.getUserId(parse_token);
        memberService.updatePassword(pwdUpdateDto.getPassword(), memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody JoinDto joinDto) {
        memberService.join(joinDto);

        return ResponseEntity.ok().build();
    }

}
