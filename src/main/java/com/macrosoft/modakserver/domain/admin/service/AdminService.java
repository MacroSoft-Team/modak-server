package com.macrosoft.modakserver.domain.admin.service;

import com.macrosoft.modakserver.domain.admin.entity.StatEntityType;
import java.time.LocalDate;
import java.util.Map;

public interface AdminService {
    void saveStatics(LocalDate date);

    Map<StatEntityType, Map<LocalDate, Long>> getStatistics();
}
