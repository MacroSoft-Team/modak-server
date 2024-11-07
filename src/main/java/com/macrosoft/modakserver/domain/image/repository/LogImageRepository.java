package com.macrosoft.modakserver.domain.image.repository;

import com.macrosoft.modakserver.domain.image.entity.LogImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogImageRepository extends JpaRepository<LogImage, Long> {
}
