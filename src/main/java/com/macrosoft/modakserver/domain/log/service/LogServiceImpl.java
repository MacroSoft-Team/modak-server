package com.macrosoft.modakserver.domain.log.service;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.image.repository.EmotionRepository;
import com.macrosoft.modakserver.domain.image.repository.LogImageRepository;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogDTO;
import com.macrosoft.modakserver.domain.log.entity.Location;
import com.macrosoft.modakserver.domain.log.entity.Log;
import com.macrosoft.modakserver.domain.log.repository.LocationRepository;
import com.macrosoft.modakserver.domain.log.repository.LogRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final LogRepository logRepository;

    @Override
    @Transactional
    public LogResponse.LogDTO addLogs(Member member, int campfirePin, LogRequest.UploadLog uploadLog) {
        // 장작 추가할 수 있는지 권한 검증
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);

        // DTO -> Entity
        Log newLog = Log.of(campfire, uploadLog);
        List<Log> existLogs = logRepository.findAllByCampfirePin(campfirePin);
        List<Log> sameEventLogs = existLogs.stream()
                .filter(existLog -> existLog.isSameEvent(newLog))
                .toList();

        // 겹치는 장작 없는 경우 새로운 장작 생성
        if (sameEventLogs.isEmpty()) {
            return LogDTO.of(logRepository.save(newLog));
        }

        // 겹치는 장작 있는 경우 겹치는 장작중에 하나 골라서 다 합치기
        List<Log> allLogs = new ArrayList<>(sameEventLogs);
        allLogs.add(newLog);
        Log primaryLog = sameEventLogs.get(0);
        mergeLogs(allLogs, primaryLog);

        // 이미 존재하는데 합치는데 사용한 나머지 장작들은 삭제하기
        for (int i = 1; i < sameEventLogs.size(); i++) {
            Log log = sameEventLogs.get(i);
            logRepository.delete(log);
        }

        return LogDTO.of(logRepository.save(primaryLog));
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

        List<Log> logs = campfire.getLogs();
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
    public LogResponse.Logs getLogs(Member member, int campfirePin, int page, int size) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);

        return null;
    }
}
