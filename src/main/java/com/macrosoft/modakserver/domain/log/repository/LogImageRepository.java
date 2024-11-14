package com.macrosoft.modakserver.domain.log.repository;

import com.macrosoft.modakserver.domain.log.entity.Log;
import com.macrosoft.modakserver.domain.log.entity.LogImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogImageRepository extends JpaRepository<LogImage, Long> {
    Page<LogImage> findAllByLog(Log log, Pageable pageable);
}
