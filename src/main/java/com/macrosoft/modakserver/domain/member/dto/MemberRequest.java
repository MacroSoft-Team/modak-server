package com.macrosoft.modakserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class MemberRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberSignIn {
        @Schema(description = "애플로부터 받은 Authorization Code", defaultValue = "c19074541137c4508ad5ab04301028c28.0.rrwuz.9laqvPNEVlUKyJ7AY-QeLw")
        private String authorizationCode;

        @Schema(description = "애플로부터 받은 ID Token.", defaultValue = "eyJraWQiOiJmaDZCczhDIiwiYWaxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcExlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLm9zZ")
        private String identityToken;

        @Schema(description = "암호화 된 UserIdentifier", defaultValue = "614c0236d8480a64d9f2214e2486317de1ede78dc59250c806650bce3cbf6ed9")
        private String encryptedUserIdentifier;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberNickname {
        @Schema(description = "변경할 닉네임", defaultValue = "새로운 닉네임")
        private String nickname;
    }

    @Data
    public static class RefreshTokenRequest {
        private String encryptedUserIdentifier;
        private String refreshToken;
    }
}
