package com.macrosoft.modakserver.domain.admin.controller;

import com.macrosoft.modakserver.domain.admin.entity.StatEntityType;
import com.macrosoft.modakserver.domain.admin.service.AdminService;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Seoul")
    @PostMapping("/statistics")
    public void saveStatics() {
        log.info("통계 저장 실행");
        adminService.saveStatics();
    }

    @GetMapping("/statistics")
    public Map<StatEntityType, Map<LocalDate, Long>> getStatistics() {
        return adminService.getStatistics();
    }

    // 전체 모닥불 개수

    // 활성 모닥불 개수 (7일 이내 이모지 반응이나 장작 업로드 있는 모닥불)

    // 전체 장작 개수

    // 전체 이미지 개수
}
