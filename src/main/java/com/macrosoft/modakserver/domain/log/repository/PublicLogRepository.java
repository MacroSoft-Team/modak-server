package com.macrosoft.modakserver.domain.log.repository;

import com.macrosoft.modakserver.domain.log.entity.PublicLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicLogRepository extends JpaRepository<PublicLog, Long> {
}
