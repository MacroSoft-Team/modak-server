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
@Tag(name = "인증 및 인가 API", description = "인증 및 인가 관련 API 입니다.")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "애플 소셜 로그인", description = "로그인을 하고 `Access Token` 과 `Refresh Token` 을 발급합니다. Apple 로부터 받은 UserId 를 SHA256으로 해시해서 `encryptedUserIdentifier` 로 보내주세요. 나머지 정보는 아직 사용하지 않습니다.")
    @PostMapping("/login/apple")
    public BaseResponse<MemberResponse.MemberLogin> signIn(
            @RequestBody MemberRequest.MemberSignIn request) {

        return BaseResponse.onSuccess(authService.login(
                SocialType.APPLE,
                request.getAuthorizationCode(),
                request.getIdentityToken(),
                request.getEncryptedUserIdentifier()));
    }

    @PostMapping("/refresh-access-token")
    @Operation(summary = "Access Token 재발급", description = "로그인을 하고 `Access Token` 과 `Refresh Token` 을 발급합니다. Apple 로부터 받은 UserId 를 SHA256으로 해시해서 `encryptedUserIdentifier` 로 보내주세요. 나머지 정보는 아직 사용하지 않습니다.")
    public BaseResponse<MemberResponse.accessToken> refreshAccessToken(
            @RequestBody MemberRequest.RefreshTokenRequest request) {

        return BaseResponse.onSuccess(authService.refreshAccessToken(request.getEncryptedUserIdentifier(), request.getRefreshToken()));
    }
}
