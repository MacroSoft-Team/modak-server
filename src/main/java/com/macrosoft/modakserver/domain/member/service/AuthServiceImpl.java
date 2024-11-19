package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.config.jwt.JwtProperties;
import com.macrosoft.modakserver.config.jwt.JwtUtil;
import com.macrosoft.modakserver.domain.campfire.repository.MemberCampfireRepository;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Avatar;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.RefreshToken;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.exception.MemberErrorCode;
import com.macrosoft.modakserver.domain.member.repository.AvatarRepository;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.domain.member.repository.RefreshTokenRepository;
import com.macrosoft.modakserver.domain.member.util.NicknameGenerator;
import com.macrosoft.modakserver.global.exception.AuthErrorCode;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final MemberCampfireRepository memberCampfireRepository;
    private final CampfireService campfireService;
    private final AvatarRepository avatarRepository;

    @Override
    @Transactional
    public MemberResponse.MemberLogin login(
            SocialType socialType, String authorizationCode, String identityToken, String encryptedUserIdentifier
    ) {
        Member member = memberRepository.findByClientId(encryptedUserIdentifier)
                .orElseGet(() -> createNewMember(encryptedUserIdentifier, socialType));

        String accessToken = jwtUtil.createAccessToken(member);
        String refreshToken = jwtUtil.createRefreshToken(member);

        updateOrCreateRefreshToken(encryptedUserIdentifier, refreshToken);

        return new MemberResponse.MemberLogin(member.getId(), accessToken, refreshToken);
    }

    @Override
    @Transactional
    public MemberResponse.AccessToken refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        jwtUtil.validateRefreshToken(refreshToken);

        // Refresh Token 에서 memberId 추출 하고 clientId 가져오기
        Long memberId = jwtUtil.getMemberId(refreshToken);
        String clientId = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND))
                .getClientId();

        RefreshToken storedRefreshToken = refreshTokenRepository.findByClientId(clientId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.MEMBER_NOT_HAVE_TOKEN));

        if (!storedRefreshToken.getToken().equals(refreshToken)) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // 새로운 Access Token 발급
        Member member = findMemberByClientId(clientId);
        String newAccessToken = jwtUtil.createAccessToken(member);

        return new MemberResponse.AccessToken(newAccessToken);
    }

    @Override
    @Transactional
    public void logout(String clientId) {
        if (refreshTokenRepository.deleteByClientId(clientId) == 0) {
            throw new CustomException(AuthErrorCode.MEMBER_NOT_HAVE_TOKEN);
        }
    }

    @Override
    @Transactional
    public void deactivate(String clientId) {
        logout(clientId);
        Member member = findMemberByClientId(clientId);

        // 1. 닉네임와 식별 정보, 디바이스 토큰 삭제
        Avatar avatar = member.getAvatar();
        avatarRepository.delete(avatar);
        member.deactivate();

        // 2. 모든 캠프파이어 나가기
        List<Integer> campfirePins = memberCampfireRepository.findAllByMember(member).stream()
                .map(memberCampfire -> memberCampfire.getCampfire().getPin())
                .toList();
        for (int campfirePin : campfirePins) {
            campfireService.leaveCampfire(member, campfirePin);
        }
    }

    private Member createNewMember(String clientId, SocialType socialType) {
        String nickname = NicknameGenerator.generateRandomNickname();
        return memberRepository.save(
                Member.builder()
                        .clientId(clientId)
                        .socialType(socialType)
                        .nickname(nickname)
                        .permissionRole(PermissionRole.CLIENT)
                        .build());
    }

    private void updateOrCreateRefreshToken(String clientId, String refreshToken) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtProperties.getRefresh_token_expiration());
        refreshTokenRepository.findByClientId(clientId)
                .ifPresentOrElse(
                        existingStoredToken -> updateRefreshToken(existingStoredToken, refreshToken, expirationDate),
                        () -> saveNewRefreshToken(clientId, refreshToken, expirationDate)
                );
    }

    private void updateRefreshToken(RefreshToken existingStoredToken, String newToken, Date expirationDate) {
        existingStoredToken.setToken(newToken);
        existingStoredToken.setExpirationDate(expirationDate);
        refreshTokenRepository.save(existingStoredToken);
    }

    private void saveNewRefreshToken(String clientId, String refreshToken, Date expirationDate) {
        RefreshToken newRefreshToken = RefreshToken.builder()
                .clientId(clientId)
                .token(refreshToken)
                .expirationDate(expirationDate)
                .build();
        refreshTokenRepository.save(newRefreshToken);
    }

    private Member findMemberByClientId(String clientId) {
        return memberRepository.findByClientId(clientId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
