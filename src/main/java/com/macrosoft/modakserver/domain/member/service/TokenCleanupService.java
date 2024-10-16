package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCleanupService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 하루 한번씩 만료된 리프레시 토큰 정보 삭제
    public void removeExpiredTokens() {
        int deletedTokenCount = refreshTokenRepository.deleteByExpirationDateBefore(new Date());
        log.info("만료되어서 삭제한 토큰 개수: {}", deletedTokenCount);
    }
}