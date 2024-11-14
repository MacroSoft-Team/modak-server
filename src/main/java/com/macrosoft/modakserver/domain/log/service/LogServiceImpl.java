package com.macrosoft.modakserver.domain.log.service;

import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.LOG_CAMPFIRE_NOT_MATCH;
import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.LOG_NOT_FOUND;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.image.repository.EmotionRepository;
import com.macrosoft.modakserver.domain.image.repository.LogImageRepository;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogId;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogOverview;
import com.macrosoft.modakserver.domain.log.entity.Location;
import com.macrosoft.modakserver.domain.log.entity.Log;
import com.macrosoft.modakserver.domain.log.repository.LocationRepository;
import com.macrosoft.modakserver.domain.log.repository.LogRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final CampfireService campfireService;
    private final MemberService memberService;
    private final EmotionRepository emotionRepository;
    private final LocationRepository locationRepository;
    private final LogImageRepository logImageRepository;
    private final CampfireRepository campfireRepository;
    private final LogRepository logRepository;

    @Override
    @Transactional
    public LogResponse.LogId addLogs(Member member, int campfirePin, LogRequest.UploadLog uploadLog) {
        // 장작 추가할 수 있는지 권한 검증
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);

        // DTO -> Entity
        Log newLog = Log.of(campfire, memberInDB, uploadLog);
        List<Log> existLogs = logRepository.findAllByCampfirePin(campfirePin);

        List<Log> sameEventLogs = existLogs.stream()
                .filter(existLog -> existLog.isSameEvent(newLog))
                .toList();

        // 겹치는 장작 없는 경우 새로운 장작 생성
        if (sameEventLogs.isEmpty()) {
            Log savedLog = logRepository.save(newLog);
//            campfire.addLog(newLog);
//            campfireRepository.save(campfire);
            registerTodayImage(campfire, newLog);
            return new LogId(savedLog.getId());
        }

        // 겹치는 장작 있는 경우 겹치는 장작중에 하나 골라서 다 합치기
        List<Log> allLogs = new ArrayList<>(sameEventLogs);
        allLogs.add(newLog);
        Log primaryLog = sameEventLogs.get(0);
        mergeLogs(allLogs, primaryLog);

        // 이미 존재하는데 합치는데 사용한 나머지 장작들은 삭제하기
        for (int i = 1; i < sameEventLogs.size(); i++) {
            Log log = sameEventLogs.get(i);
//            campfire.removeLog(log);
            logRepository.delete(log);
        }

        Log savedLog = logRepository.save(primaryLog);
//        campfire.addLog(newLog);
//        campfireRepository.save(campfire);
        registerTodayImage(campfire, primaryLog);
        return new LogId(savedLog.getId());
    }

    // 올라가는 장작에서 랜덤으로 하나 골라서 오늘의 사진 등록
    private void registerTodayImage(Campfire campfire, Log newlog) {
        if (campfire.getTodayImage() == null) {
            List<LogImage> logImages = newlog.getLogImages();
            LogImage todayImage = logImages.get((int) (Math.random() * logImages.size()));
            campfire.setTodayImage(todayImage);
            campfireRepository.save(campfire);
        }
    }

    private void mergeLogs(List<Log> allLogs, Log primaryLog) {
        Location mergedLocation = calcMergedLocation(allLogs);
        String mergedAddress = calcMergedAddress(allLogs);
        List<LogImage> mergedLogImages = calcMergeLogImages(allLogs);
        LocalDateTime mergedStartAt = calcMergeStartAt(allLogs);
        LocalDateTime mergedEndAt = calcMergeEndAt(allLogs);
        mergedLocation.setAddress(mergedAddress);

        primaryLog.update(mergedLocation, mergedStartAt, mergedEndAt, mergedLogImages);
    }

    private Location calcMergedLocation(List<Log> logs) {
        Location mergedLocation = logs.get(0).getLocation();
        for (int i = 1; i < logs.size(); i++) {
            Location location = logs.get(i).getLocation();
            mergedLocation = mergedLocation.merge(location);
        }
        return mergedLocation;
    }

    private String calcMergedAddress(List<Log> logs) {
        Log earliestLog = logs.get(0);
        for (Log log : logs) {
            if (log.getStartAt().isBefore(earliestLog.getStartAt())) {
                earliestLog = log;
            }
        }
        return earliestLog.getLocation().getAddress();
    }

    private List<LogImage> calcMergeLogImages(List<Log> logs) {
        List<LogImage> mergedLogImages = new ArrayList<>();
        for (Log log : logs) {
            mergedLogImages.addAll(log.getLogImages());
        }
        return mergedLogImages;
    }

    private LocalDateTime calcMergeStartAt(List<Log> logs) {
        LocalDateTime earliestTime = logs.get(0).getStartAt();
        for (Log log : logs) {
            if (log.getStartAt().isBefore(earliestTime)) {
                earliestTime = log.getStartAt();
            }
        }
        return earliestTime;
    }

    private LocalDateTime calcMergeEndAt(List<Log> logs) {
        LocalDateTime latestTime = logs.get(0).getEndAt();
        for (Log log : logs) {
            if (log.getEndAt().isAfter(latestTime)) {
                latestTime = log.getEndAt();
            }
        }
        return latestTime;
    }

    @Override
    public LogResponse.LogMetadataList getLogsMetadata(Member member, int campfirePin) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);

        List<LogResponse.LogMetadata> logMetadataList = new ArrayList<>();

        Set<Log> logs = campfire.getLogs();
        for (Log log : logs) {
            Location location = log.getLocation();
            LogResponse.LogMetadata logMetadata = new LogResponse.LogMetadata(
                    log.getStartAt(),
                    log.getEndAt(),
                    location.getAddress(),
                    location.getMinLatitude(),
                    location.getMaxLatitude(),
                    location.getMinLongitude(),
                    location.getMaxLongitude()
            );
            logMetadataList.add(logMetadata);
        }

        return new LogResponse.LogMetadataList(logMetadataList);
    }

    @Override
    public LogResponse.LogOverviews getLogOverviews(Member member, int campfirePin, int page, int size) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);

        Pageable pageable = PageRequest.of(page, size, Sort.by("startAt").descending());
        Page<Log> logs = logRepository.findAllByCampfirePin(campfirePin, pageable);

        List<LogOverview> overviews = new ArrayList<>();
        for (Log log : logs) {
            Pageable pageable1 = PageRequest.of(0, 8, Sort.by("takenAt").ascending());
            Page<LogImage> logImages = logImageRepository.findAllByLog(log, pageable1);
            List<String> imageNames = logImages.stream()
                    .map(LogImage::getName)
                    .toList();

            LogOverview overview = new LogOverview(
                    log.getId(),
                    log.getStartAt(),
                    log.getLocation().getAddress(),
                    imageNames
            );
            overviews.add(overview);
        }
        return new LogResponse.LogOverviews(overviews, logs.hasNext());
    }

    @Override
    public LogResponse.LogDetails getLogDetails(Member member, int campfirePin, Long logId, int page, int size) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);
        Log log = getLog(logId);
        validateLogInCampfire(log, campfire);

        Pageable pageable = PageRequest.of(page, size, Sort.by("takenAt").ascending());
        Page<LogImage> logImages = logImageRepository.findAllByLog(log, pageable);

        return new LogResponse.LogDetails(
                log.getId(),
                logImages.stream()
                        .map(logImage -> new ImageResponse.ImageName(logImage.getId(), logImage.getName()))
                        .toList(),
                logImages.hasNext()
        );
    }

    @Override
    public LogId removeLog(Member member, int campfirePin, Long logId) {
        return null;
    }

    private Log getLog(Long logId) {
        return logRepository.findById(logId)
                .orElseThrow(() -> new CustomException(LOG_NOT_FOUND));
    }

    private void validateLogInCampfire(Log log, Campfire campfire) {
        if (!log.getCampfire().equals(campfire)) {
            throw new CustomException(LOG_CAMPFIRE_NOT_MATCH);
        }
    }
}
