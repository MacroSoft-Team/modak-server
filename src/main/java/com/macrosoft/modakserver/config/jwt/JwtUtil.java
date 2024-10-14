package com.macrosoft.modakserver.config.jwt;

import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.global.exception.AuthErrorCode;
import com.macrosoft.modakserver.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
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
        Date expiration = new Date(new Date().getTime() + expiredTime);

        return Jwts.builder()
                .subject(member.getNickname())
                .claim("tokenType", "access")
                .claim("memberId", member.getId())
                .claim("clientId", member.getClientId())
                .claim("permissionRole", member.getPermissionRole())
                .issuedAt(new Date())
                .expiration(expiration)
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
        Date expiration = new Date(new Date().getTime() + expiredTime);

        return Jwts.builder()
                .subject(member.getNickname())
                .claim("tokenType", "refresh")
                .claim("memberId", member.getId())
                .claim("clientId", member.getClientId())
                .claim("permissionRole", member.getPermissionRole())
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰 유효성 검사
     * @param token
     * @return boolean
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info(e.getLocalizedMessage());
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info(e.getLocalizedMessage());
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info(e.getLocalizedMessage());
            throw new CustomException(AuthErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info(e.getLocalizedMessage());
            throw new CustomException(AuthErrorCode.EMPTY_TOKEN);
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            throw new CustomException(AuthErrorCode.TOKEN_VALIDATION_FAIL);
        }
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
