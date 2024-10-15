package com.macrosoft.modakserver.config.jwt;

import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.exception.MemberErrorCode;
import com.macrosoft.modakserver.global.exception.AuthErrorCode;
import com.macrosoft.modakserver.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;
    private final UserDetailsService customUserDetailService;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    /**
     * Access Token 생성
     * @param member
     * @return accessToken
     */
    public String createAccessToken(Member member) {
        Long expiredTime = Optional.ofNullable(jwtProperties.getAccess_token_expiration())
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_TOKEN_TYPE));
        Date expiration = new Date(System.currentTimeMillis() + expiredTime);

        return Jwts.builder()
                .subject(member.getId().toString())
                .issuedAt(new Date())
                .expiration(expiration)
                .claim("tokenType", "access")
                .claim("memberId", member.getId())
                .claim("permissionRole", member.getPermissionRole())
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     * @param member
     * @return refreshToken
     */
    public String createRefreshToken(Member member) {
        Long expiredTime = Optional.ofNullable(jwtProperties.getRefresh_token_expiration())
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_TOKEN_TYPE));
        Date expiration = new Date(System.currentTimeMillis() + expiredTime);

        return Jwts.builder()
                .subject(member.getId().toString())
                .issuedAt(new Date())
                .expiration(expiration)
                .claim("tokenType", "refresh")
                .claim("memberId", member.getId())
                .claim("clientId", member.getClientId())
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰 유효성 검사
     * @param token
     * @return boolean
     */
    public void validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (SecurityException | MalformedJwtException e) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(AuthErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(AuthErrorCode.EMPTY_TOKEN);
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.TOKEN_VALIDATION_FAIL);
        }
    }

    public Authentication getAuthentication(String token) {
        Long memberId = this.getMemberId(token);
        UserDetails userDetails = Optional.ofNullable(customUserDetailService.loadUserByUsername(memberId.toString()))
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 토큰에서 memberId 추출
     * @param token
     * @return memberId
     */
    public Long getMemberId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("memberId", Long.class);
    }
}
