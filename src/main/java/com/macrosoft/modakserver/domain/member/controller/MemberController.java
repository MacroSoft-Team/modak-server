package com.macrosoft.modakserver.domain.member.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.member.dto.MemberRequest;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "Member API", description = "회원 정보 관련 API 입니다.")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원들의 닉네임 가져오기", description = "회원들의 닉네임을 가져옵니다. 쿼리 파라미터를 비워둘 경우 본인의 닉네임을 가져옵니다.")
    @GetMapping("/nickname")
    public BaseResponse<List<MemberResponse.MemberNickname>> getMembersNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "memberIds", required = false) List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            memberIds = List.of(userDetails.getMember().getId());
        }
        return BaseResponse.onSuccess(memberService.getNicknames(memberIds));
    }

    @Operation(summary = "내 닉네임 변경", description = "회원 본인의 닉네임을 변경합니다.")
    @PatchMapping("/nickname")
    public BaseResponse<MemberResponse.MemberNickname> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("nickname") String nickname
    ) {
        Member member = userDetails.getMember();
        return BaseResponse.onSuccess(memberService.updateNickname(member, nickname));
    }

    @Operation(summary = "회원들의 닉네임과 아바타 가져오기", description = "회원들의 닉네임과 아바타 정보를 가져옵니다. 쿼리 파라미터를 비워둘 경우 본인의 닉네임과 아바타를 가져옵니다. `CampfireMemberDetail` 화면과 `Campfires` 화면에서 사용됩니다.")
    @GetMapping("/nickname-avatar")
    public BaseResponse<List<MemberResponse.MemberNicknameAvatar>> getMembersNicknameAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "memberIds", required = false) List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            memberIds = List.of(userDetails.getMember().getId());
        }
        return BaseResponse.onSuccess(memberService.getNicknamesAndAvatars(memberIds));
    }

    @Operation(summary = "아바타 변경하기", description = "회원 본인의 아바타를 변경합니다.")
    @PatchMapping("/avatar")
    public BaseResponse<MemberResponse.MemberAvatar> updateAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MemberRequest.MemberAvatar avatar
    ) {
        Member member = userDetails.getMember();
        return BaseResponse.onSuccess(
                memberService.updateAvatar(member, avatar.hatType(), avatar.faceType(), avatar.topType()));
    }
}
