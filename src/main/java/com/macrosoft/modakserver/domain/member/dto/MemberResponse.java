package com.macrosoft.modakserver.domain.member.dto;

public class MemberResponse {
    public record MemberLogin(
            Long memberId,
            String accessToken,
            String refreshToken) {
    }

    public record MemberNickname(
            Long memberId,
            String nickname) {
    }

    public record AccessToken(
            String accessToken) {
    }

    public record MemberNicknameAvatar(
            Long memberId,
            String nickname,
            MemberAvatar avatar) {
    }

    public record MemberAvatar(
            int hatType,
            int faceType,
            int topType) {
    }
}
