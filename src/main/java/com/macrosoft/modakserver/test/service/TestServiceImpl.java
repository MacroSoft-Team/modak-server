package com.macrosoft.modakserver.test.service;

import static java.util.stream.Collectors.toSet;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfo;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfirePin;
import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.domain.campfire.exception.CampfireErrorCode;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.ImageInfo;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.UploadLog;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.service.LogService;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.exception.MemberErrorCode;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.domain.member.service.AuthService;
import com.macrosoft.modakserver.global.exception.CustomException;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
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
    private final EntityManager entityManager;

    @Override
    public List<Member> get() {
        return memberRepository.findAll();
    }

    @Transactional
    @Override
    public CampfireResponse.CampfireMain addMockData(Member member) {
        String newCampfireName = "매크로";
        int count = (int) campfireService.getMyCampfires(member).campfireInfos().stream()
                .map(CampfireInfo::campfireName)
                .filter(name -> name.contains("매크로"))
                .count();
        if (count > 0) {
            newCampfireName += count;
        }

        CampfirePin campfirePin = campfireService.createCampfire(member, newCampfireName);

        Campfire campfire = campfireRepository.findByPin(campfirePin.campfirePin())
                .orElseThrow(() -> new CustomException(CampfireErrorCode.CAMPFIRE_NOT_FOUND_BY_PIN));

        int pin = campfirePin.campfirePin();
        if (count == 0) {
            campfireRepository.updateCampfirePinById(111111, campfire.getId());
            campfireRepository.flush();
            entityManager.clear();
            campfire = campfireRepository.findById(campfire.getId())
                    .orElseThrow(() -> new CustomException(CampfireErrorCode.CAMPFIRE_NOT_FOUND_BY_PIN));
            log.info("updateCampfirePinById: {}", campfire.getId());
            log.info("실제 PIN: {}", campfire.getPin());
            pin = 111111;
        }

        for (int i = 0; i < 4; i++) {
            log.info("addMockData: {}", i);
            String authorizationCode = String.valueOf(UUID.randomUUID());
            String identityToken = String.valueOf(UUID.randomUUID());
            String encryptedUserIdentifier = String.valueOf(UUID.randomUUID());
            Long newMemberId = authService.login(SocialType.APPLE, authorizationCode, identityToken,
                    encryptedUserIdentifier).memberId();
            log.info("newMemberId: {}", newMemberId);
            Member newMember = memberRepository.findById(newMemberId)
                    .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
            log.info("newMember: {}", newMember);
            log.info("pin: {}, 실제 pin: {}", pin, campfire.getPin());
            campfireService.joinCampfire(newMember, pin, newCampfireName);
            log.info("joinCampfire: {}", i);
            logService.addLogs(newMember, pin, testUploadLogs.get(i));
            log.info("addLogs: {}", i);
        }

        return new CampfireResponse.CampfireMain(
                pin,
                newCampfireName,
                campfireService.getTodayImage(campfire),
                campfire.getMemberCampfires().stream()
                        .map(MemberCampfire::getMember)
                        .map(Member::getId)
                        .collect(toSet())
        );
    }

    private final List<UploadLog> testUploadLogs = List.of(
            new LogRequest.UploadLog(
                    new LogResponse.LogMetadata(
                            LocalDateTime.of(2023, 3, 3, 3, 3, 3),
                            LocalDateTime.of(2023, 3, 3, 8, 3, 3),
                            "포항시 북구",
                            35.99,
                            129.35,
                            130.0,
                            130.5
                    ),
                    List.of(
                            new ImageInfo(
                                    "dev/c90830f0-7e69-41ba-92b5-713ed5253221.jpeg",
                                    35.98, 129.34, LocalDateTime.of(2023, 3, 3, 3, 3, 3)),
                            new ImageInfo("dev/7e622a14-9cbe-49d8-be87-5ff51e549cc8.jpeg", 36.0, 129.36,
                                    LocalDateTime.of(2023, 3, 3, 8, 3, 3))
                    )
            ),
            new LogRequest.UploadLog(
                    new LogResponse.LogMetadata(
                            LocalDateTime.of(2023, 3, 3, 6, 3, 3),
                            LocalDateTime.of(2023, 3, 3, 11, 3, 3),
                            "포항시 북구",
                            36.0,
                            129.37,
                            130.0,
                            130.5
                    ),
                    List.of(
                            new ImageInfo("dev/af1105bf-85bb-4f79-9c9e-4d3062336150.jpeg", 36.01, 129.38,
                                    LocalDateTime.of(2023, 3, 3, 6, 3, 3)),
                            new ImageInfo("dev/9e3bb690-cf83-4258-8ced-f941a648098a.jpg", 36.02, 129.39,
                                    LocalDateTime.of(2023, 3, 3, 11, 3, 3))
                    )
            ),
            new LogRequest.UploadLog(
                    new LogResponse.LogMetadata(
                            LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                            LocalDateTime.of(2021, 1, 1, 6, 1, 1),
                            "서울시 강남구",
                            37.52,
                            127.04,
                            127.0,
                            127.1
                    ),
                    List.of(
                            new ImageInfo("dev/636e5833-6c5b-4b17-a22f-07c98d7b0935.jpg", 37.53, 127.05,
                                    LocalDateTime.of(2021, 1, 1, 1, 1, 1)),
                            new ImageInfo("dev/30f32344-9c59-4507-a52d-9f9e399c5dfd.jpg", 37.54, 127.06,
                                    LocalDateTime.of(2021, 1, 1, 6, 1, 1))
                    )
            ),
            new LogRequest.UploadLog(
                    new LogResponse.LogMetadata(
                            LocalDateTime.of(2021, 5, 5, 10, 10, 10),
                            LocalDateTime.of(2021, 5, 5, 15, 10, 10),
                            "충청북도 청주시",
                            36.64,
                            127.45,
                            127.4,
                            127.5
                    ),
                    List.of(
                            new ImageInfo("dev/9347176d-b98f-4e9c-af0d-4e46e075c363.jpg", 36.65, 127.46,
                                    LocalDateTime.of(2021, 5, 5, 10, 10, 10)),
                            new ImageInfo("dev/fa4b41a7-8324-4aad-83a6-3f7703f4ed0f.jpg", 36.66, 127.47,
                                    LocalDateTime.of(2021, 5, 5, 15, 10, 10))
                    )
            )
    );
}
