package com.macrosoft.modakserver.domain.member.dto;

import lombok.*;

public class MemberResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberLogin {
        private Long memberId;
        private String accessToken;
        private String refreshToken;
        private boolean isServiced;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {
        private Long memberId;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberId {
        private String memberId;
    }
}
