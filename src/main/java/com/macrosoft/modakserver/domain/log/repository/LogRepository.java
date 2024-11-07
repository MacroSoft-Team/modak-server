package com.macrosoft.modakserver.domain.log.repository;

import com.macrosoft.modakserver.domain.log.entity.Log;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByCampfirePin(int campfirePin);
}
