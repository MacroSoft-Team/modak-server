package com.macrosoft.modakserver.domain.campfire.service;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.log.entity.Log;
import com.macrosoft.modakserver.domain.log.entity.LogImage;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
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

        for (Campfire campfire : campfireRepository.findAll()) {
            try {
                if (processCampfire(campfire)) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                exceptionCount++;
                log.error("모닥불 {} 처리 중 오류가 발생했습니다: {}", campfire.getId(), e.getMessage());
            }
        }

        log.info("오늘의 사진 등록 서비스 종료. {}개의 모닥불에 오늘의 사진이 변경 또는 등록 되었습니다.\n"
                        + "{}개의 모닥불은 오늘의 사진 선정에 실패했습니다.\n"
                        + "{}개의 모닥불의 오늘의 사진 변경에서 오류가 발생했습니다.",
                successCount, failCount, exceptionCount);
    }

    private boolean processCampfire(Campfire campfire) {
        LogImage yesterdayImage = campfire.getTodayImage();

        if (campfire.getLogs().isEmpty()) {
            return false;
        }

        Log pickedLog = pickRandomLog(campfire.getLogs(), yesterdayImage);
        if (pickedLog == null || pickedLog.getLogImages().isEmpty()) {
            return false;
        }

        LogImage logImage = pickRandomLogImage(pickedLog.getLogImages());
        campfire.setTodayImage(logImage);
        campfireRepository.save(campfire);
        return true;
    }

    // 어제의 이미지와 같은 장작에 속하는 이미지는 선택하지 않음
    private Log pickRandomLog(Set<Log> logs, LogImage yesterdayImage) {
        if (yesterdayImage == null) {
            return null;
        }

        Log yesterdayImageLog = yesterdayImage.getLog();
        Set<Log> filteredLogs = logs.stream()
                .filter(log -> !log.equals(yesterdayImageLog))
                .collect(Collectors.toSet());

        // 어재의 장작이 아닌 장작이 없다 -> 모닥불에 장작이 하나인 경우 이므로 그냥 원래 장작에서 선택
        if (filteredLogs.isEmpty()) {
            return pickRandomLogFromSet(logs);
        }

        return pickRandomLogFromSet(filteredLogs);
    }

    private Log pickRandomLogFromSet(Set<Log> logs) {
        return logs.stream()
                .skip(new Random().nextInt(logs.size()))
                .findFirst()
                .orElse(null);
    }

    private LogImage pickRandomLogImage(List<LogImage> logImages) {
        if (logImages.isEmpty()) {
            return null;
        }
        return logImages.get((int) (Math.random() * logImages.size()));
    }
}