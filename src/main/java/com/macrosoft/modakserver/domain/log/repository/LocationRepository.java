package com.macrosoft.modakserver.domain.log.repository;

import com.macrosoft.modakserver.domain.log.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
