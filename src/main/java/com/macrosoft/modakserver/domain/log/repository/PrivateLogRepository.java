package com.macrosoft.modakserver.domain.log.repository;

import com.macrosoft.modakserver.domain.log.entity.PrivateLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateLogRepository extends JpaRepository<PrivateLog, Long> {
    int deleteAllByMemberId(Long memberId);
}
