package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.config.jwt.JwtUtil;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse.AccessToken;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.RefreshToken;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.domain.member.repository.RefreshTokenRepository;
import com.macrosoft.modakserver.global.exception.AuthErrorCode;
import com.macrosoft.modakserver.global.exception.CustomException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.junit.jupiter.api.*;
import java.util.Optional;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class AuthServiceImplTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthServiceImpl authService;

    private String encryptedUserIdentifier;
    private String authorizationCode;
    private String identityToken;
    private SocialType socialType;

    @BeforeEach
    void setUp() {
        encryptedUserIdentifier = "encryptedUserIdentifier";
        authorizationCode = "auth-code";
        identityToken = "identityToken";
        socialType = SocialType.APPLE;
    }

    @Nested
    class loginTests {
        @Test
        void 로그인_성공_반환값_검사() {
            // when
            MemberResponse.MemberLogin memberLogin = authService.login(socialType, authorizationCode, identityToken, encryptedUserIdentifier);

            // then
            String accessToken = memberLogin.getAccessToken();
            String refreshToken = memberLogin.getRefreshToken();
            assertThat(jwtUtil.isExpired(accessToken)).isFalse();
            assertThat(jwtUtil.isExpired(refreshToken)).isFalse();
            assertThat(jwtUtil.getMemberId(accessToken)).isEqualTo(memberLogin.getMemberId());
        }

        @Test
        void 로그인_성공_Member_테이블_검사() {
            // when
            MemberResponse.MemberLogin memberLogin = authService.login(socialType, authorizationCode, identityToken, encryptedUserIdentifier);

            // then
            Long memberId = memberLogin.getMemberId();
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
            Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByClientId(encryptedUserIdentifier);
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
            assertThatThrownBy(() -> authService.login(socialType, authorizationCode, identityToken, encryptedUserIdentifier))
                    .isInstanceOf(CustomException.class);
        }
    }

    @Nested
    class refreshAccessTokenTests {
        @Test
        void 엑세스토큰_재발급_성공() {
            // given
            MemberResponse.MemberLogin memberLogin = authService.login(socialType, authorizationCode, identityToken, encryptedUserIdentifier);
            String refreshToken = memberLogin.getRefreshToken();

            // when
            MemberResponse.AccessToken accessToken = authService.refreshAccessToken(socialType, encryptedUserIdentifier, refreshToken);

            // then
            String accessTokenString = accessToken.getAccessToken();
            assertThat(accessTokenString).isNotNull();
            assertThat(jwtUtil.isExpired(accessTokenString)).isFalse();
            assertThat(jwtUtil.getMemberId(accessTokenString)).isEqualTo(memberLogin.getMemberId());
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
            assertThatThrownBy(() ->authService.logout("invalid" + encryptedUserIdentifier))
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
            Optional<Member> optionalMember = memberRepository.findById(memberLogin.getMemberId());
            assertThat(optionalMember).isPresent();
            Member member = optionalMember.get();
            assertThat(member.getClientId()).isEmpty();
            assertThat(member.getNickname()).isEqualTo("알 수 없음");
            assertThat(member.getDeviceToken()).isNull();

            assertThat(memberRepository.findByClientId(encryptedUserIdentifier)).isEmpty();
            assertThat(refreshTokenRepository.findByClientId(encryptedUserIdentifier)).isEmpty();
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