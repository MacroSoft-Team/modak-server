package com.macrosoft.modakserver.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.macrosoft.modakserver.config.jwt.JwtUtil;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.PrivateLogInfo;
import com.macrosoft.modakserver.domain.log.entity.PrivateLog;
import com.macrosoft.modakserver.domain.log.repository.PrivateLogRepository;
import com.macrosoft.modakserver.domain.log.service.LogService;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.RefreshToken;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.domain.member.repository.RefreshTokenRepository;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private LogService logService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PrivateLogRepository privateLogRepository;
    @Autowired
    private JwtUtil jwtUtil;

    private String encryptedUserIdentifier;
    private String authorizationCode;
    private String identityToken;
    private SocialType socialType;
    private Member member;

    @BeforeEach
    void setUp() {
        encryptedUserIdentifier = "encryptedUserIdentifier";
        authorizationCode = "auth-code";
        identityToken = "identityToken";
        socialType = SocialType.APPLE;
        member = memberRepository.save(Member.builder()
                .clientId("clientId0")
                .socialType(SocialType.APPLE)
                .nickname("nickname0")
                .permissionRole(PermissionRole.CLIENT)
                .build()
        );
    }

    @Nested
    class loginTests {
        @Test
        void 로그인_성공_반환값_검사() {
            // when
            MemberResponse.MemberLogin memberLogin = authService.login(socialType, authorizationCode, identityToken,
                    encryptedUserIdentifier);

            // then
            String accessToken = memberLogin.accessToken();
            String refreshToken = memberLogin.refreshToken();
            assertThat(jwtUtil.isExpired(accessToken)).isFalse();
            assertThat(jwtUtil.isExpired(refreshToken)).isFalse();
            assertThat(jwtUtil.getMemberId(accessToken)).isEqualTo(memberLogin.memberId());
        }

        @Test
        void 로그인_성공_Member_테이블_검사() {
            // when
            MemberResponse.MemberLogin memberLogin = authService.login(socialType, authorizationCode, identityToken,
                    encryptedUserIdentifier);

            // then
            Long memberId = memberLogin.memberId();
            Optional<Member> optionalMember = memberRepository.findById(memberId);
            assertThat(optionalMember).isPresent();
            Member member = optionalMember.get();

            assertThat(member.getClientId()).isEqualTo(encryptedUserIdentifier);
            assertThat(member.getSocialType()).isEqualTo(socialType);
            assertThat(member.getNickname()).isNotNull();
        }

        @Test
        void 로그인_성공_RefreshToken_테이블_검사() {
            // when
            authService.login(socialType, authorizationCode, identityToken, encryptedUserIdentifier);

            // then
            Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByClientId(
                    encryptedUserIdentifier);
            assertThat(optionalRefreshToken).isPresent();
            RefreshToken refreshToken = optionalRefreshToken.get();

            assertThat(refreshToken.getToken()).isNotNull();
            assertThat(jwtUtil.isExpired(refreshToken.getToken())).isFalse();
        }

        // TODO: 나머지 테스트 메소드에는 OAuth 인증 로직 Mocking 해서 통과하도록 테스트 하고, 이 테스트는 Mocking 안하고 테스트
        @Test
        @Disabled
        void 로그인_실패_OAuth_인증_실패() {
            // when then
            assertThatThrownBy(
                    () -> authService.login(socialType, authorizationCode, identityToken, encryptedUserIdentifier))
                    .isInstanceOf(CustomException.class);
        }
    }

    @Nested
    class refreshAccessTokenTests {
        @Test
        void 엑세스토큰_재발급_성공() {
            // given
            MemberResponse.MemberLogin memberLogin = authService.login(socialType, authorizationCode, identityToken,
                    encryptedUserIdentifier);
            String refreshToken = memberLogin.refreshToken();

            // when
            MemberResponse.AccessToken accessToken = authService.refreshAccessToken(refreshToken);

            // then
            String accessTokenString = accessToken.accessToken();
            assertThat(accessTokenString).isNotNull();
            assertThat(jwtUtil.isExpired(accessTokenString)).isFalse();
            assertThat(jwtUtil.getMemberId(accessTokenString)).isEqualTo(memberLogin.memberId());
        }
    }

    @Nested
    class logoutTests {
        private MemberResponse.MemberLogin memberLogin;

        @BeforeEach
        void setUp() {
            // given
            memberLogin = authService.login(socialType, authorizationCode, identityToken, encryptedUserIdentifier);
        }

        @Test
        void 로그아웃_성공_리프레시토큰_삭제() {
            // when
            authService.logout(encryptedUserIdentifier);

            // then
            assertThat(refreshTokenRepository.findByClientId(encryptedUserIdentifier)).isEmpty();
        }

        @Test
        void 로그아웃_실패_유저ID_불일치() {
            // when
            assertThatThrownBy(() -> authService.logout("invalid" + encryptedUserIdentifier))
                    .isInstanceOf(CustomException.class);

            // then
            assertThat(refreshTokenRepository.findByClientId(encryptedUserIdentifier)).isNotEmpty();
        }
    }

    @Nested
    class deactivateTests {
        private MemberResponse.MemberLogin memberLogin;

        @BeforeEach
        void setUp() {
            // given
            memberLogin = authService.login(socialType, authorizationCode, identityToken, encryptedUserIdentifier);
        }

        @Test
        void 회원탈퇴_성공_멤버정보와_리프레시토큰_삭제() {
            // when
            authService.deactivate(encryptedUserIdentifier);

            // then
            Optional<Member> optionalMember = memberRepository.findById(memberLogin.memberId());
            assertThat(optionalMember).isPresent();
            Member member = optionalMember.get();

            assertThat(member.getClientId()).isEmpty();
            assertThat(member.getNickname()).isEqualTo("알 수 없음");
            assertThat(member.getDeviceToken()).isNull();

            assertThat(memberRepository.findByClientId(encryptedUserIdentifier)).isEmpty();
            assertThat(refreshTokenRepository.findByClientId(encryptedUserIdentifier)).isEmpty();
        }

        @Test
        @Disabled
        void 회원탈퇴_성공_프라이빗로그_삭제() {
            // given
            Optional<Member> optionalMember = memberRepository.findById(memberLogin.memberId());
            assertThat(optionalMember).isPresent();
            Member member = optionalMember.get();

            LogRequest.PrivateLogInfos privateLogInfos = new LogRequest.PrivateLogInfos(
                    List.of(new PrivateLogInfo("주소", 1.0, 2.0, 3.0, 4.0, LocalDateTime.now(),
                            LocalDateTime.now().plusMinutes(10))
                    )
            );

            List<Long> logIds = logService.uploadPrivateLog(member, privateLogInfos).logIds();
            Optional<PrivateLog> optionalPrivateLog = privateLogRepository.findById(logIds.get(0));
            assertThat(optionalPrivateLog).isPresent();

            // when
            authService.deactivate(encryptedUserIdentifier);

            // then
//            assertThat(member.getPrivateLogs()).isEmpty();
            Optional<PrivateLog> optionalPrivateLog1 = privateLogRepository.findById(logIds.get(0));
            assertThat(optionalPrivateLog1).isNotPresent();
        }

        @Test
        void 회원탈퇴_실패_유저ID_불일치() {
            // when
            assertThatThrownBy(() -> authService.deactivate("invalid" + encryptedUserIdentifier))
                    .isInstanceOf(CustomException.class);

            // then
            assertThat(memberRepository.findByClientId(encryptedUserIdentifier)).isNotEmpty();
            assertThat(refreshTokenRepository.findByClientId(encryptedUserIdentifier)).isNotEmpty();
        }
    }
}