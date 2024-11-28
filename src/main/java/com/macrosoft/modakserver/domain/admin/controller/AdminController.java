package com.macrosoft.modakserver.domain.admin.controller;

import com.macrosoft.modakserver.domain.admin.entity.StatEntityType;
import com.macrosoft.modakserver.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Seoul")
    @Operation(deprecated = true)
    @PutMapping("/statistics")
    public void saveStatics() {
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        adminService.saveStatics(date);
    }

    @Operation(deprecated = true)
    @GetMapping("/statistics")
    public Map<StatEntityType, Map<LocalDate, Long>> getStatistics() {
        return adminService.getStatistics();
    }
}
