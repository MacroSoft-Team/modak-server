package com.macrosoft.modakserver.domain.log.repository;

import com.macrosoft.modakserver.domain.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicLogRepository extends JpaRepository<Log, Long> {
}
