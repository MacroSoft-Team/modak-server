package com.macrosoft.modakserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberRequest {

    public record MemberSignIn(
            @Schema(description = "애플로부터 받은 Authorization Code", defaultValue = "c19074541137c4508ad5ab04301028c28.0.rrwuz.9laqvPNEVlUKyJ7AY-QeLw")
            String authorizationCode,

            @Schema(description = "애플로부터 받은 ID Token.", defaultValue = "eyJraWQiOiJmaDZCczhDIiwiYWaxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcExlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLm9zZ")
            String identityToken,

            @Schema(description = "암호화 된 UserIdentifier", defaultValue = "614c0236d8480a64d9f2214e2486317de1ede78dc59250c806650bce3cbf6ed9")
            String encryptedUserIdentifier
    ) {
    }

    public record RefreshTokenRequest(
            @Schema(description = "Refresh Token", defaultValue = "refreshToken")
            String refreshToken
    ) {
    }
}