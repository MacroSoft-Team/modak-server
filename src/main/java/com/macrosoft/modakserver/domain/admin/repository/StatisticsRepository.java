package com.macrosoft.modakserver.domain.admin.repository;

import com.macrosoft.modakserver.domain.admin.entity.Statistics;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsRepository extends JpaRepository<Statistics, LocalDate> {
}
