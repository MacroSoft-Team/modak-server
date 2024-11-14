package com.macrosoft.modakserver.test.service;

import static java.util.stream.Collectors.toSet;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfo;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfirePin;
import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageName;
import com.macrosoft.modakserver.domain.image.service.ImageService;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.ImageInfo;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.UploadLog;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogDetails;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogMetadata;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogOverview;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogOverviews;
import com.macrosoft.modakserver.domain.log.service.LogService;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.exception.MemberErrorCode;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.domain.member.service.AuthService;
import com.macrosoft.modakserver.global.exception.CustomException;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final MemberRepository memberRepository;
    private final CampfireRepository campfireRepository;
    private final CampfireService campfireService;
    private final AuthService authService;
    private final LogService logService;
    private final ImageService imageService;
    private final EntityManager entityManager;
    private static final Random RANDOM = new Random();

    private static final List<String> IMAGE_PATHS = List.of(
            "dev/c90830f0-7e69-41ba-92b5-713ed5253221.jpeg",
            "dev/7e622a14-9cbe-49d8-be87-5ff51e549cc8.jpeg",
            "dev/af1105bf-85bb-4f79-9c9e-4d3062336150.jpeg",
            "dev/9e3bb690-cf83-4258-8ced-f941a648098a.jpg",
            "dev/636e5833-6c5b-4b17-a22f-07c98d7b0935.jpg",
            "dev/30f32344-9c59-4507-a52d-9f9e399c5dfd.jpg",
            "dev/9347176d-b98f-4e9c-af0d-4e46e075c363.jpg",
            "dev/fa4b41a7-8324-4aad-83a6-3f7703f4ed0f.jpg"
    );

    @Transactional
    @Override
    public CampfireResponse.CampfireMain addMockData(Member member) {
        int count = getCampfireNameCountWith(member, "매크로");
        String newCampfireName = getMockCampfireName(count);

        CampfirePin campfirePin = campfireService.createCampfire(member, newCampfireName);
        int pin = campfirePin.campfirePin();
        Campfire campfire = campfireService.findCampfireByPin(pin);

        if (count == 0) {
            pin = 111111;
            campfire = changeCampfirePinTo(pin, campfire);
        }

        List<Member> members = makeMockCampfireMemberAndLogsBy(pin, newCampfireName, 4);

        for (Member memberInCampfire : members) {
            int randomLogCount = RANDOM.nextInt(3) + 3;

            makeMockLogsBy(memberInCampfire, pin, randomLogCount);
        }

        for (Member memberInCampfire : members) {
            makeMockEmotesBy(memberInCampfire, pin);
        }

        return new CampfireResponse.CampfireMain(
                pin,
                newCampfireName,
                campfireService.getTodayImageDTO(campfire),
                campfire.getMemberCampfires().stream()
                        .map(MemberCampfire::getMember)
                        .map(Member::getId)
                        .collect(toSet())
        );
    }

    private void makeMockEmotesBy(Member member, int pin) {
        LogOverviews logOverviews = logService.getLogOverviews(member, pin, 0, 100);
        List<Long> logIds = logOverviews.logOverviews().stream()
                .map(LogOverview::logId)
                .toList();

        List<Long> imageIds = new ArrayList<>();
        for (Long logId : logIds) {
            LogDetails logDetails = logService.getLogDetails(member, pin, logId, 0, 100);
            imageIds.addAll(logDetails.images().stream()
                    .map(ImageName::imageId)
                    .toList());
        }

        for (Long imageId : imageIds) {
            int randomNumber = RANDOM.nextInt(3);
            if (randomNumber < 2) {
                imageService.emotion(member, pin, imageId, getRandomEmotion());
            }
        }
    }

    private String getRandomEmotion() {
        List<String> emotion = List.of("😀", "😅", "😱", "😭", "☠️", "❤️", "💦", "😰", "🤪", "😎", "😡", "skrrrr", "스껄깝");
        return emotion.get(RANDOM.nextInt(emotion.size()));
    }

    private List<Member> makeMockCampfireMemberAndLogsBy(int pin, String newCampfireName, int memberCount) {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < memberCount; i++) {
            String authorizationCode = UUID.randomUUID().toString();
            String identityToken = UUID.randomUUID().toString();
            String encryptedUserIdentifier = UUID.randomUUID().toString();
            Long newMemberId = authService.login(SocialType.APPLE, authorizationCode, identityToken,
                    encryptedUserIdentifier).memberId();
            Member newMember = memberRepository.findById(newMemberId)
                    .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
            members.add(newMember);
            campfireService.joinCampfire(newMember, pin, newCampfireName);
        }
        return members;
    }

    private void makeMockLogsBy(Member member, int pin, int count) {
        for (int j = 0; j < count; j++) {
            logService.addLogs(member, pin, generateMockUploadLog());
        }
    }

    private Campfire changeCampfirePinTo(int pin, Campfire campfire) {
        campfireRepository.updateCampfirePinById(pin, campfire.getId());
        campfireRepository.flush();
        entityManager.clear();
        return campfireService.findCampfireByPin(pin);
    }

    private int getCampfireNameCountWith(Member member, String campfireName) {
        return (int) campfireService.getMyCampfires(member).campfireInfos().stream()
                .map(CampfireInfo::campfireName)
                .filter(name -> name.contains(campfireName))
                .count();
    }

    private String getMockCampfireName(int count) {
        String mockCampfireName = "매크로";
        if (count > 0) {
            mockCampfireName += count;
        }
        return mockCampfireName;
    }

    private LogRequest.UploadLog generateMockUploadLog() {
        LocalDateTime startAt = getRandomDateTime();
        int randomTime = RANDOM.nextInt(48) + 1;
        LocalDateTime endAt = startAt.plusHours(randomTime);
        String address = generateRandomAddress();
        double minLatitude = Math.round((RANDOM.nextDouble() * 10 + 30) * 1_000_000.0) / 1_000_000.0;
        double maxLatitude =
                Math.round((minLatitude + 0.01 * randomTime * RANDOM.nextDouble()) * 1_000_000.0) / 1_000_000.0;
        double minLongitude = Math.round((RANDOM.nextDouble() * 10 + 120) * 1_000_000.0) / 1_000_000.0;
        double maxLongitude =
                Math.round((minLongitude + 0.01 * randomTime * RANDOM.nextDouble()) * 1_000_000.0) / 1_000_000.0;

        LogMetadata mockLogMetadata = new LogResponse.LogMetadata(startAt, endAt, address, minLatitude, maxLatitude,
                minLongitude, maxLongitude);
        List<ImageInfo> mockImageInfos = new ArrayList<>();
        int randomInt = RANDOM.nextInt(50) + 3;
        for (int i = 0; i < randomInt; i++) {
            mockImageInfos.add(
                    generateMockImageInfo(minLatitude, maxLatitude, minLongitude, maxLongitude, startAt, endAt));
        }
        return new UploadLog(mockLogMetadata, mockImageInfos);
    }

    private static LocalDateTime getRandomDateTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fourYearsAgo = now.minusYears(4);

        long daysBetween = java.time.Duration.between(fourYearsAgo, now).toDays();
        int randomDays = RANDOM.nextInt((int) daysBetween + 1);

        return fourYearsAgo.plusDays(randomDays)
                .plusHours(RANDOM.nextInt(24))
                .plusMinutes(RANDOM.nextInt(60))
                .plusSeconds(RANDOM.nextInt(60));
    }

    private String generateRandomAddress() {
        List<String> prefix = List.of("서울시", "대전시", "대구시", "부산시", "인천시", "광주시", "울산시", "세종시", "성남시", "용인시", "포항시");
        List<String> suffix = List.of("강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구",
                "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구");
        return prefix.get(RANDOM.nextInt(prefix.size())) + " " + suffix.get(RANDOM.nextInt(suffix.size()));
    }

    private ImageInfo generateMockImageInfo(double minLa, double maxLa, double minLo, double maxLo,
                                            LocalDateTime startAt, LocalDateTime endAt) {
        String imagePath = IMAGE_PATHS.get(RANDOM.nextInt(IMAGE_PATHS.size()));
        double latitude = Math.round((RANDOM.nextDouble() * (maxLa - minLa) + minLa) * 1_000_000.0) / 1_000_000.0;
        double longitude = Math.round((RANDOM.nextDouble() * (maxLo - minLo) + minLo) * 1_000_000.0) / 1_000_000.0;

        long secondsBetween = java.time.Duration.between(startAt, endAt).getSeconds();
        long randomSeconds = RANDOM.nextLong(secondsBetween + 1);
        LocalDateTime takenAt = startAt.plusSeconds(randomSeconds);
        return new ImageInfo(imagePath, latitude, longitude, takenAt);
    }
}
