package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 하루 한번씩 만료된 리프레시 토큰 정보 삭제
    public void removeExpiredTokens() {
        refreshTokenRepository.deleteByExpirationDateBefore(new Date());
    }
}