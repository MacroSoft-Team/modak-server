package com.macrosoft.modakserver.domain.log.service;

import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.EMOTE_EMPTY;
import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.EMOTE_TOO_LONG;
import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.EMOTION_NOT_FOUND;
import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.EMOTION_NOT_UPLOAD_USER;
import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.LOG_CAMPFIRE_NOT_MATCH;
import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.LOG_IMAGE_NOT_FOUND;
import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.LOG_IMAGE_NOT_IN_CAMPFIRE;
import static com.macrosoft.modakserver.domain.log.exception.LogErrorCode.LOG_NOT_FOUND;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.file.service.FileService;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.ImageDTO;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.ImageIds;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogId;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogOverview;
import com.macrosoft.modakserver.domain.log.entity.Emotion;
import com.macrosoft.modakserver.domain.log.entity.Location;
import com.macrosoft.modakserver.domain.log.entity.Log;
import com.macrosoft.modakserver.domain.log.entity.LogImage;
import com.macrosoft.modakserver.domain.log.repository.EmotionRepository;
import com.macrosoft.modakserver.domain.log.repository.LogImageRepository;
import com.macrosoft.modakserver.domain.log.repository.LogRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import com.macrosoft.modakserver.global.exception.CustomException;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public static final int MAX_EMOTE_LENGTH = 10;
    private final CampfireService campfireService;
    private final MemberService memberService;
    private final LogImageRepository logImageRepository;
    private final CampfireRepository campfireRepository;
    private final LogRepository logRepository;
    private final FileService fileService;
    private final EmotionRepository emotionRepository;
    private final EntityManager entityManager;

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
                        .map(logImage -> new LogResponse.ImageName(logImage.getId(), logImage.getName()))
                        .toList(),
                logImages.hasNext()
        );
    }

    @Override
    @Transactional
    public LogId removeLog(Member member, int campfirePin, Long logId) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);
        Log log = getLog(logId);
        validateLogInCampfire(log, campfire);

        List<Long> logImageIds = log.getLogImages().stream()
                .map(LogImage::getId)
                .toList();
        removeImages(member, campfirePin, logId, logImageIds);
        logRepository.delete(log);
        return new LogId(logId);
    }

    @Override
    public Log getLog(Long logId) {
        return logRepository.findById(logId)
                .orElseThrow(() -> new CustomException(LOG_NOT_FOUND));
    }

    private void validateLogInCampfire(Log log, Campfire campfire) {
        if (!log.getCampfire().equals(campfire)) {
            throw new CustomException(LOG_CAMPFIRE_NOT_MATCH);
        }
    }

    @Override
    public LogResponse.ImageDetail getImageDetail(Member member, int campfirePin, Long imageId) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);
        LogImage logImage = getLogImage(imageId);
        validateLogImageInCampfire(campfire, logImage);
        return LogResponse.ImageDetail.of(logImage);
    }

    @Override
    @Transactional
    public ImageDTO emotion(Member member, int campfirePin, Long imageId, String emotion) {
        validateEmoteLength(emotion);
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);
        LogImage logImage = getLogImage(imageId);
        validateLogImageInCampfire(campfire, logImage);

        Optional<Emotion> optionalExistingEmotion = findOneEmotion(member, logImage);

        if (optionalExistingEmotion.isPresent()) {
            Emotion existingEmotion = optionalExistingEmotion.get();
            existingEmotion.updateEmotion(emotion);
            emotionRepository.save(existingEmotion);
        } else {
            Emotion newEmotion = Emotion.of(emotion, logImage, memberInDB);
            emotionRepository.save(newEmotion);
            logImage.addEmote(newEmotion);
        }

        return ImageDTO.of(logImage);
    }

    @Override
    @Transactional
    public ImageDTO deleteEmotion(Member member, int campfirePin, Long imageId) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);
        LogImage logImage = getLogImage(imageId);
        validateLogImageInCampfire(campfire, logImage);
        Set<Emotion> emotion = findEmotion(member, logImage);
        for (Emotion e : emotion) {
            validateEmotionUploader(e, memberInDB);
        }
        emotionRepository.deleteAll(emotion);
        entityManager.flush();
        logImage = getLogImage(imageId);
        return ImageDTO.of(logImage);
    }

    private Set<Emotion> findEmotion(Member member, LogImage logImage) {
        Set<Emotion> emotions = emotionRepository.findAllByMemberAndLogImage(member, logImage);
        if (emotions.isEmpty()) {
            throw new CustomException(EMOTION_NOT_FOUND);
        }
        return emotions;
    }

    private Optional<Emotion> findOneEmotion(Member member, LogImage logImage) {
        return emotionRepository.findByMemberAndLogImage(member, logImage);
    }

    private void validateEmotionUploader(Emotion emotion, Member member) {
        if (!emotion.getMember().equals(member)) {
            throw new CustomException(EMOTION_NOT_UPLOAD_USER);
        }
    }

    private void validateEmoteLength(String emote) {
        if (emote == null || emote.isBlank()) {
            throw new CustomException(EMOTE_EMPTY);
        }
        if (emote.length() > MAX_EMOTE_LENGTH) {
            throw new CustomException(EMOTE_TOO_LONG);
        }
    }

    @Override
    @Transactional
    public ImageIds removeImages(Member member, int campfirePin, Long logId, List<Long> imageIds) {
        List<Long> deletedLogImages = new ArrayList<>();
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = campfireService.findCampfireByPin(campfirePin);
        campfireService.validateMemberInCampfire(memberInDB, campfire);

        List<LogImage> logImages = getLogImages(imageIds);
        Log log = getLog(logId);
        validateLogImagesInCampfire(campfire, logImages);

        for (Long imageId : imageIds) {
            LogImage logImage = getLogImage(imageId);
            String imageName = logImage.getName();
            removeImage(logImage);
            fileService.deleteImageFromS3(imageName);
            deletedLogImages.add(imageId);
        }

        if (log.getLogImages().isEmpty()) {
            logRepository.delete(log);
        }

        return new ImageIds(deletedLogImages);
    }

    private List<LogImage> getLogImages(List<Long> imageIds) {
        return imageIds.stream()
                .map(this::getLogImage)
                .toList();
    }

    private void validateLogImagesInCampfire(Campfire campfire, List<LogImage> logImages) {
        for (LogImage logImage : logImages) {
            validateLogImageInCampfire(campfire, logImage);
        }
    }

    private void removeImage(LogImage logImage) {
        logImage.getLog().removeLogImage(logImage);
        logImageRepository.delete(logImage);
    }

    public LogImage getLogImage(Long imageId) {
        return logImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(LOG_IMAGE_NOT_FOUND));
    }

    private void validateLogImageInCampfire(Campfire campfire, LogImage logImage) {
        if (!logImage.getLog().getCampfire().equals(campfire)) {
            throw new CustomException(LOG_IMAGE_NOT_IN_CAMPFIRE);
        }
    }
}
