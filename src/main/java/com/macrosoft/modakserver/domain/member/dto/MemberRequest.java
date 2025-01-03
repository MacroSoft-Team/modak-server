package com.macrosoft.modakserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberRequest {

    public record MemberSignIn(
            @Schema(description = "애플로부터 받은 Authorization Code", example = "c19074541137c4508ad5ab04301028c28.0.rrwuz.9laqvPNEVlUKyJ7AY-QeLw")
            String authorizationCode,

            @Schema(description = "애플로부터 받은 ID Token.", example = "eyJraWQiOiJmaDZCczhDIiwiYWaxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcExlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLm9zZ")
            String identityToken,

            @Schema(description = "암호화 된 UserIdentifier", example = "614c0236d8480a64d9f2214e2486317de1ede78dc59250c806650bce3cbf6ed9")
            String encryptedUserIdentifier
    ) {
    }

    public record RefreshTokenRequest(
            @Schema(description = "Refresh Token", example = "refreshToken")
            String refreshToken
    ) {
    }

    public record MemberAvatar(
            int hatType,
            int faceType,
            int topType) {
    }
}