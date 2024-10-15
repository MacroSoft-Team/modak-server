package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.config.jwt.JwtUtil;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.domain.member.util.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public MemberResponse.MemberLogin login(SocialType socialType, String authorizationCode, String identityToken, String encryptedUserIdentifier) {
        Member member = memberRepository.findByClientId(encryptedUserIdentifier)
                .orElseGet(() -> createNewMember(encryptedUserIdentifier, socialType));

        // 1. 토큰 유효 검사, 토큰 만료시 재발급
        String accessToken = generateAccessToken(member);
        String refreshToken = generateRefreshToken(member);

        return MemberResponse.MemberLogin.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isServiced(true)
                .build();
    }

    private Member createNewMember(String clientId, SocialType socialType) {
        String nickname = makeRandomNickname();
        return memberRepository.save(
                Member.builder()
                        .clientId(clientId)
                        .socialType(socialType)
                        .nickname(nickname)
                        .permissionRole(PermissionRole.CLIENT)
                        .build());
    }

    private String makeRandomNickname() {
        return NicknameGenerator.generateRandomNickname();
    }

    private String generateAccessToken(Member member) {
        return jwtUtil.createAccessToken(member);
    }

    private String generateRefreshToken(Member member) {
        return jwtUtil.createRefreshToken(member);
    }
}
