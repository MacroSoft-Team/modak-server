package com.macrosoft.modakserver.domain.member.controller;

import com.macrosoft.modakserver.domain.member.dto.MemberRequest;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.service.AuthService;
import com.macrosoft.modakserver.global.BaseResponse;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "애플 소셜 로그인", description = "Apple 로부터 받은 UserId 를 SHA256으로 해시해서 보내주세요. 나머지 정보는 아직 사용하지 않습니다.")
    @PostMapping("/login/apple")
    public BaseResponse<MemberResponse.MemberLogin> signIn(@RequestBody MemberRequest.MemberSignIn request) {
        return BaseResponse.onSuccess(authService.login(
                SocialType.APPLE,
                request.getAuthorizationCode(),
                request.getIdentityToken(),
                request.getEncryptedUserIdentifier()));
    }
}
