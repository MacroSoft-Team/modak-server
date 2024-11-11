package com.macrosoft.modakserver.domain.campfire.service;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.log.entity.Log;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodayImageService {
    private final CampfireRepository campfireRepository;

    @Transactional
    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Seoul") // 하루 한번씩 서비스 전체 모닥불의 오늘의 사진 업데이트
    public void registerTodayImage() {
        log.info("오늘의 사진 등록 서비스 시작");
        int successCount = 0;
        int failCount = 0;
        int exceptionCount = 0;

        // 캠프파이어 중 랜덤으로 하나 골라서 오늘의 사진 등록
        for (Campfire campfire : campfireRepository.findAll()) {
            try {
                LogImage yesterdayImage = campfire.getTodayImage();

                if (campfire.getLogs().isEmpty()) {
                    failCount++;
                    continue;
                }

                Log pickedLog = pickRandomLog(campfire.getLogs(), yesterdayImage);
                if (pickedLog == null) {
                    failCount++;
                    continue;
                }

                List<LogImage> logImages = pickedLog.getLogImages();
                if (logImages.isEmpty()) {
                    failCount++;
                    continue;
                }

                LogImage logImage = pickRandomLogImage(logImages);
                campfire.setTodayImage(logImage);
                campfireRepository.save(campfire);
                successCount++;
            } catch (Exception e) {
                exceptionCount++;
                log.error("모닥불 {} 처리 중 오류가 발생했습니다: {}", campfire.getId(), e.getMessage());
            }
        }
        log.info("오늘의 사진 등록 서비스 종료. {}개의 모닥불에 오늘의 사진이 변경 또는 등록 되었습니다.\n"
                + "{}개의 모닥불은 오늘의 사진 선정에 실패했습니다.\n"
                + "{}개의 모닥불의 오늘의 사진 변경에서 오류가 발생했습니다.", successCount, failCount, exceptionCount);
    }

    private Log pickRandomLog(List<Log> logs, LogImage yesterdayImage) {
        if (yesterdayImage == null) {
            if (logs.isEmpty()) {
                return null;
            }
            return logs.get((int) (Math.random() * logs.size()));
        }

        Log yesterdayImageLog = yesterdayImage.getLog();
        List<Log> filteredLogs = logs.stream()
                .filter(log -> !log.equals(yesterdayImageLog))
                .toList();

        if (filteredLogs.isEmpty()) {
            return logs.get((int) (Math.random() * logs.size()));
        }

        return filteredLogs.get((int) (Math.random() * filteredLogs.size())); // 랜덤으로 하나 선택
    }

    private LogImage pickRandomLogImage(List<LogImage> logImages) {
        if (logImages.isEmpty()) {
            return null; // 빈 리스트 처리
        }
        return logImages.get((int) (Math.random() * logImages.size()));
    }
}