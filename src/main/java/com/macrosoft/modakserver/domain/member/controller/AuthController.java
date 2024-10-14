package com.macrosoft.modakserver.domain.member.controller;

import com.macrosoft.modakserver.domain.member.dto.MemberRequest.MemberSignUp;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.global.BaseResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class AuthController {

    private final MemberService memberService;

    @Operation(summary = "애플 회원 가입 API", description = "Apple 로부터 받은 UserId 를 SHA256으로 해시해서 보내주세요. 나머지 정보는 아직 사용하지 않습니다.")
    @PostMapping("/signup/apple")
    public BaseResponse<Member> signup(@RequestBody MemberSignUp request) {
        Member member = memberService.signup(
                SocialType.APPLE,
                request.getAuthorizationCode(),
                request.getIdentityToken(),
                request.getHashedUserId());
        return BaseResponse.onSuccess(member);
    }
}
