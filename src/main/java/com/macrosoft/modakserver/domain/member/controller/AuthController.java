package com.macrosoft.modakserver.domain.member.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.member.dto.MemberRequest;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.service.AuthService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "인증 및 인가 관련 API 입니다.")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인", description = "로그인을 하고 `Access Token` 과 `Refresh Token` 을 발급합니다. 유저를 식별할 수 있는 값을 (Apple: user) SHA256으로 해시해서 `encryptedUserIdentifier` 로 보내주세요. 나머지 정보는 아직 사용하지 않습니다.")
    @PostMapping("/{socialType}/login")
    public BaseResponse<MemberResponse.MemberLogin> signIn(
            @PathVariable("socialType") SocialType socialType,
            @RequestBody MemberRequest.MemberSignIn request) {

        return BaseResponse.onSuccess(authService.login(
                socialType,
                request.getAuthorizationCode(),
                request.getIdentityToken(),
                request.getEncryptedUserIdentifier()));
    }

    @PostMapping("/{socialType}/refresh-access-token")
    @Operation(summary = "Access Token 재발급", description = "`Access Token` 이 만료됐을 경우 호출해 주세요. `Refresh Token` 과 `encryptedUserIdentifier` 로 `Access Token` 을 재발급합니다.")
    public BaseResponse<MemberResponse.accessToken> refreshAccessToken(
            @PathVariable("socialType") SocialType socialType,
            @RequestBody MemberRequest.RefreshTokenRequest request) {

        return BaseResponse.onSuccess(authService.refreshAccessToken(socialType, request.getEncryptedUserIdentifier(), request.getRefreshToken()));
    }
}
