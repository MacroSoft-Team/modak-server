package com.macrosoft.modakserver.domain.member.repository;

import com.macrosoft.modakserver.domain.member.entity.RefreshToken;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByClientId(String clientId);
    int deleteByExpirationDateBefore(Date now);
    int deleteByClientId(String clientId);
}
