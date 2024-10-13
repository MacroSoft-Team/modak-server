package com.macrosoft.modakserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class MemberRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberSignUp {
        @Schema(description = "애플로부터 받은 Authorization Code", defaultValue = "eyJhbGciOi")
        private String authorizationCode;

        @Schema(description = "애플로부터 받은 ID Token.", defaultValue = "eyJhbGciOi")
        private String identityToken;

        @Schema(description = "해시된 User Id", defaultValue = "hashedUserId123")
        private String hashedUserId;
    }

}
