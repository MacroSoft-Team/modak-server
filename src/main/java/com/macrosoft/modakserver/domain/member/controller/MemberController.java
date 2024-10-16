package com.macrosoft.modakserver.domain.member.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "Member API", description = "회원 정보 관련 API 입니다.")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 닉네임 가져오기", description = "회원의 닉네임을 가져옵니다.")
    @GetMapping("/nickname")
    public BaseResponse<MemberResponse.MemberNickname> getMemberNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        return BaseResponse.onSuccess(memberService.getMemberNickname(member));
    }

    @Operation(summary = "회원 닉네임 변경", description = "회원의 닉네임을 변경합니다.")
    @PatchMapping("/nickname")
    public BaseResponse<MemberResponse.MemberNickname> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String nickname) {

        Member member = userDetails.getMember();
        return BaseResponse.onSuccess(memberService.updateNickname(member, nickname));
    }
}
