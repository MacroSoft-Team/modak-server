package com.macrosoft.modakserver.domain.image.repository;

import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.log.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogImageRepository extends JpaRepository<LogImage, Long> {
    Page<LogImage> findAllByLog(Log log, Pageable pageable);
}
