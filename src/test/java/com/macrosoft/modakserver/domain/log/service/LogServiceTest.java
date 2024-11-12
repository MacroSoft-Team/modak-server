package com.macrosoft.modakserver.domain.log.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.ImageInfo;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.UploadLog;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogOverview;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogOverviews;
import com.macrosoft.modakserver.domain.log.entity.Log;
import com.macrosoft.modakserver.domain.log.repository.LogRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LogServiceTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LogService logService;

    @Autowired
    private CampfireService campfireService;

    @Autowired
    private MemberRepository memberRepository;

    private Member member0;
    private Member member1;
    private List<LogRequest.UploadLog> uploadLogList;
    @Autowired
    private LogRepository logRepository;

    @BeforeEach
    void setUp() {
        List<Member> members = createMembers();
        member0 = members.get(0);
        member1 = members.get(1);
        uploadLogList = createUploadLogList();
    }

    private List<Member> createMembers() {
        return List.of(
                memberRepository.save(Member.builder()
                        .clientId("clientId0")
                        .socialType(SocialType.APPLE)
                        .nickname("nickname0")
                        .permissionRole(PermissionRole.CLIENT)
                        .build()),
                memberRepository.save(Member.builder()
                        .clientId("clientId1")
                        .socialType(SocialType.APPLE)
                        .nickname("nickname1")
                        .permissionRole(PermissionRole.CLIENT)
                        .build()
                ));
    }

    private List<LogRequest.UploadLog> createUploadLogList() {
        return List.of(
                new LogRequest.UploadLog(
                        new LogResponse.LogMetadata(
                                LocalDateTime.of(2023, 3, 3, 3, 3, 3),
                                LocalDateTime.of(2025, 5, 5, 5, 5, 5),
                                "포항시 북구",
                                40.0,
                                60.0,
                                130.0,
                                150.0
                        ),
                        List.of(
                                new ImageInfo("image0.jpg",
                                        10.0, 20.0, LocalDateTime.of(2023, 3, 3, 3, 3, 3)),
                                new ImageInfo("image1.jpg", 20.0, 30.0, LocalDateTime.of(2025, 5, 5, 5, 5, 5))
                        )
                ),
                new LogRequest.UploadLog(
                        new LogResponse.LogMetadata(
                                LocalDateTime.of(2022, 2, 2, 2, 2, 2),
                                LocalDateTime.of(2024, 4, 4, 4, 4, 4),
                                "부산시 중구",
                                30.0,
                                50.0,
                                120.0,
                                140.0
                        ),
                        List.of(
                                new ImageInfo("image2.jpg", 15.0, 25.0, LocalDateTime.of(2022, 2, 2, 2, 2, 2)),
                                new ImageInfo("image3.jpg", 25.0, 35.0, LocalDateTime.of(2024, 4, 4, 4, 4, 4))
                        )
                ),
                new LogRequest.UploadLog( // 위 두개와 시간과 날짜 모두 겹치지 않는다.
                        new LogResponse.LogMetadata(
                                LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                                LocalDateTime.of(2022, 2, 2, 2, 2, 2),
                                "서울시 강남구",
                                10.0,
                                20.0,
                                100.0,
                                110.0
                        ),
                        List.of(
                                new ImageInfo("image4.jpg", 5.0, 15.0, LocalDateTime.of(2021, 1, 1, 1, 1, 1)),
                                new ImageInfo("image5.jpg", 10.0, 20.0, LocalDateTime.of(2022, 2, 2, 2, 2, 2))
                        )
                ),
                new LogRequest.UploadLog( // 모든 데이터가 겹친다.
                        new LogResponse.LogMetadata(
                                LocalDateTime.of(2020, 1, 1, 1, 1, 1),
                                LocalDateTime.of(2026, 2, 2, 2, 2, 2),
                                "지구",
                                15.0,
                                45.0,
                                105.0,
                                135.0
                        ),
                        List.of(
                                new ImageInfo("image6.jpg", 10.0, 20.0, LocalDateTime.of(2020, 1, 1, 1, 1, 1)),
                                new ImageInfo("image7.jpg", 20.0, 30.0, LocalDateTime.of(2026, 2, 2, 2, 2, 2))
                        )
                )
        );
    }

    @Nested
    class AddLogOverviewsTests {
        @Test
        void 장작을_업로드한다_Log_엔티티_검사() {
            // given
            UploadLog uploadLog = uploadLogList.get(0);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();

            // when
            LogResponse.LogId logId = logService.addLogs(member0, campfirePin, uploadLog);

            // then
            Log logsInDB = logRepository.findById(logId.logId()).orElseThrow();
            List<String> imageNames = logsInDB.getLogImages().stream().map(LogImage::getName).toList();
            assertThat(imageNames).isEqualTo(
                    uploadLog.imageInfos().stream().map(ImageInfo::imageName).toList());
            assertThat(logsInDB.getEndAt()).isEqualTo(uploadLog.logMetadata().endAt());
            assertThat(logsInDB.getStartAt()).isEqualTo(uploadLog.logMetadata().startAt());
            assertThat(logsInDB.getLocation().getAddress()).isEqualTo(uploadLog.logMetadata().address());
            assertThat(logsInDB.getLocation().getMinLatitude()).isEqualTo(uploadLog.logMetadata().minLatitude());
            assertThat(logsInDB.getLocation().getMaxLatitude()).isEqualTo(uploadLog.logMetadata().maxLatitude());
            assertThat(logsInDB.getLocation().getMinLongitude()).isEqualTo(uploadLog.logMetadata().minLongitude());
            assertThat(logsInDB.getLocation().getMaxLongitude()).isEqualTo(uploadLog.logMetadata().maxLongitude());
            assertThat(logsInDB.getCampfire().getPin()).isEqualTo(campfirePin);
            assertThat(logsInDB.getCampfire().getName()).isEqualTo(campfireName);
        }

        @Test
        void 멤버가_모닥불에_없을_때_업로드를_시도하면_예외_발생() {
            // given
            UploadLog uploadLog = uploadLogList.get(0);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            campfireService.leaveCampfire(member0, campfirePin);

            // when then
            assertThatThrownBy(
                    () -> logService.addLogs(member0, campfirePin, uploadLog));
        }

        @Test
        void 핀번호_없을때_예외발생() {
            // given
            UploadLog uploadLog = uploadLogList.get(0);
            String campfireName = "모닥불";
            int campfirePin = 1;
            campfireService.createCampfire(member0, campfireName);

            // when then
            assertThatThrownBy(
                    () -> logService.addLogs(member0, campfirePin, uploadLog));
        }

        @Test
        void 메타데이터가_겹치는_장작_존재할떄_장작을_합친다() {
            // given
            UploadLog uploadLog0 = uploadLogList.get(0);
            UploadLog uploadLog1 = uploadLogList.get(1);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            logService.addLogs(member0, campfirePin, uploadLog0);
            campfireService.joinCampfire(member1, campfirePin, campfireName);

            // when
            logService.addLogs(member1, campfirePin, uploadLog1);

            // then
            List<Log> logsInDB = logRepository.findAllByCampfirePin(campfirePin);
            assertThat(logsInDB.size()).isEqualTo(1);
            Log logInDB = logsInDB.get(0);
            assertThat(logInDB.getEndAt()).isEqualTo(uploadLog0.logMetadata().endAt());
            assertThat(logInDB.getStartAt()).isEqualTo(uploadLog1.logMetadata().startAt());
            assertThat(logInDB.getLocation().getAddress()).isEqualTo(uploadLog1.logMetadata().address());
            assertThat(logInDB.getLocation().getMinLatitude()).isEqualTo(uploadLog1.logMetadata().minLatitude());
            assertThat(logInDB.getLocation().getMaxLatitude()).isEqualTo(uploadLog0.logMetadata().maxLatitude());
            assertThat(logInDB.getLocation().getMinLongitude()).isEqualTo(uploadLog1.logMetadata().minLongitude());
            assertThat(logInDB.getLocation().getMaxLongitude()).isEqualTo(uploadLog0.logMetadata().maxLongitude());
            assertThat(logInDB.getCampfire().getPin()).isEqualTo(campfirePin);
            assertThat(logInDB.getCampfire().getName()).isEqualTo(campfireName);
            assertThat(logInDB.getLogImages().size()).isEqualTo(4);
            assertThat(logInDB.getLogImages().stream().map(LogImage::getName))
                    .containsExactlyInAnyOrderElementsOf(
                            uploadLogList.subList(0, 2).stream() // uploadLog0, uploadLog1만 사용
                                    .flatMap(uploadLog -> uploadLog.imageInfos().stream().map(ImageInfo::imageName))
                                    .toList()
                    );
        }

        @Test
        void 메타데이터가_겹치는_장작_존재하지_않을때_장작을_합치지_않는다() {
            // given
            UploadLog uploadLog0 = uploadLogList.get(0);
            UploadLog uploadLog1 = uploadLogList.get(2);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            logService.addLogs(member0, campfirePin, uploadLog0);
            campfireService.joinCampfire(member1, campfirePin, campfireName);

            // when
            logService.addLogs(member1, campfirePin, uploadLog1);

            // then
            List<Log> logsInDB = logRepository.findAllByCampfirePin(campfirePin);
            assertThat(logsInDB.size()).isEqualTo(2);
        }

        @Test
        void 메타데이터가_겹치는_장작이_두개_이상일때_모두_합친다() {
            // given
            UploadLog uploadLog0 = uploadLogList.get(0);
            UploadLog uploadLog1 = uploadLogList.get(1);
            UploadLog uploadLog2 = uploadLogList.get(2);
            UploadLog uploadLog3 = uploadLogList.get(3);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            logService.addLogs(member0, campfirePin, uploadLog0);
            logService.addLogs(member0, campfirePin, uploadLog1);
            logService.addLogs(member0, campfirePin, uploadLog2);
            campfireService.joinCampfire(member1, campfirePin, campfireName);

            // when
            logService.addLogs(member1, campfirePin, uploadLog3);

            // then
            List<Log> logsInDB = logRepository.findAllByCampfirePin(campfirePin);
            assertThat(logsInDB.size()).isEqualTo(1);
            Log logInDB = logsInDB.get(0);
            assertThat(logInDB.getEndAt()).isEqualTo(uploadLog3.logMetadata().endAt());
            assertThat(logInDB.getStartAt()).isEqualTo(uploadLog3.logMetadata().startAt());
            assertThat(logInDB.getLocation().getAddress()).isEqualTo(uploadLog3.logMetadata().address());
            assertThat(logInDB.getLocation().getMinLatitude()).isEqualTo(uploadLog2.logMetadata().minLatitude());
            assertThat(logInDB.getLocation().getMaxLatitude()).isEqualTo(uploadLog0.logMetadata().maxLatitude());
            assertThat(logInDB.getLocation().getMinLongitude()).isEqualTo(uploadLog2.logMetadata().minLongitude());
            assertThat(logInDB.getLocation().getMaxLongitude()).isEqualTo(uploadLog0.logMetadata().maxLongitude());
            assertThat(logInDB.getCampfire().getPin()).isEqualTo(campfirePin);
            assertThat(logInDB.getCampfire().getName()).isEqualTo(campfireName);
            assertThat(logInDB.getLogImages().size()).isEqualTo(8);
            assertThat(logInDB.getLogImages().stream().map(LogImage::getName)).containsExactlyInAnyOrderElementsOf(
                    uploadLogList.stream()
                            .flatMap(uploadLog -> uploadLog.imageInfos().stream().map(ImageInfo::imageName))
                            .toList()
            );
        }

        @Test
        void 장작을_처음_넣으면_오늘의_이미지가_생긴다() {
            // given
            UploadLog uploadLog = uploadLogList.get(0);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();

            // when
            logService.addLogs(member0, campfirePin, uploadLog);

            // then
            Campfire campfire = campfireService.findCampfireByPin(campfirePin);
            assertThat(campfire.getTodayImage()).isNotNull();
        }

        @Test
        void 장작을_처음_넣는게_아니면_오늘의_이미지가_바뀌지_않는다() {
            // given
            UploadLog uploadLog0 = uploadLogList.get(0);
            UploadLog uploadLog1 = uploadLogList.get(1);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            logService.addLogs(member0, campfirePin, uploadLog0);
            Campfire campfire = campfireService.findCampfireByPin(campfirePin);
            entityManager.refresh(campfire);
            LogImage todayImage = campfire.getTodayImage();
            assertThat(todayImage).isNotNull();

            // when
            logService.addLogs(member0, campfirePin, uploadLog1);
            entityManager.refresh(campfire);

            // then
            assertThat(campfire.getTodayImage()).isEqualTo(todayImage);
        }
    }

    @Nested
    class GetLogOverviewsMetadataTest {
        @Test
        void 멤버가_모닥불에_없을_때_메타데이터를_가져오려고_하면_예외_발생() {
            // given
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            campfireService.leaveCampfire(member0, campfirePin);

            // when then
            assertThatThrownBy(
                    () -> logService.getLogsMetadata(member0, campfirePin));
        }

        @Test
        void 장작의_메타데이터_가져온다() {
            // given
            UploadLog uploadLog = uploadLogList.get(0);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            logService.addLogs(member0, campfirePin, uploadLog);

            Campfire campfire = campfireService.findCampfireByPin(campfirePin);
            entityManager.refresh(campfire); // campfire 엔티티를 최신 상태로 업데이트해 연관된 엔티티들이 최신 상태로 유지

            // when
            LogResponse.LogMetadataList logsMetadata = logService.getLogsMetadata(member0, campfirePin);

            // then
            assertThat(logsMetadata.logMetadataList().size()).isEqualTo(1);
            LogResponse.LogMetadata logMetadata = logsMetadata.logMetadataList().get(0);
            assertThat(logMetadata.endAt()).isEqualTo(uploadLog.logMetadata().endAt());
            assertThat(logMetadata.startAt()).isEqualTo(uploadLog.logMetadata().startAt());
            assertThat(logMetadata.address()).isEqualTo(uploadLog.logMetadata().address());
            assertThat(logMetadata.minLatitude()).isEqualTo(uploadLog.logMetadata().minLatitude());
            assertThat(logMetadata.maxLatitude()).isEqualTo(uploadLog.logMetadata().maxLatitude());
            assertThat(logMetadata.minLongitude()).isEqualTo(uploadLog.logMetadata().minLongitude());
            assertThat(logMetadata.maxLongitude()).isEqualTo(uploadLog.logMetadata().maxLongitude());
        }
    }

    @Nested
    class GetLogOverviewsTests {
        @Test
        void 멤버가_모닥불에_없을_때_장작을_가져오려고_하면_예외_발생() {
            // given
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            campfireService.leaveCampfire(member0, campfirePin);

            // when then
            assertThatThrownBy(
                    () -> logService.getLogOverviews(member0, campfirePin, 0, 10));
        }

        @Test
        void 최신순의_날짜로_장작을_가져온다() {
            // given
            UploadLog uploadLog0 = uploadLogList.get(0);
            UploadLog uploadLog2 = uploadLogList.get(2);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            logService.addLogs(member0, campfirePin, uploadLog0);
            logService.addLogs(member0, campfirePin, uploadLog2);

            // when
            LogOverviews logOverviews = logService.getLogOverviews(member0, campfirePin, 0, 10);

            // then
            LogOverview log0 = logOverviews.logOverviews().get(0);
            LogOverview log1 = logOverviews.logOverviews().get(1);
            assertThat(log0.startAt()).isAfter(log1.startAt());
        }

        @Test
        void 페이지네이션() {
            // given
            UploadLog uploadLog0 = uploadLogList.get(0);
            UploadLog uploadLog2 = uploadLogList.get(2);
            String campfireName = "모닥불";
            int campfirePin = campfireService.createCampfire(member0, campfireName).campfirePin();
            logService.addLogs(member0, campfirePin, uploadLog0);
            logService.addLogs(member0, campfirePin, uploadLog2);

            // when
            LogOverviews logOverviews1 = logService.getLogOverviews(member0, campfirePin, 0, 1);
            LogOverviews logOverviews2 = logService.getLogOverviews(member0, campfirePin, 1, 1);
            LogOverviews logOverviews3 = logService.getLogOverviews(member0, campfirePin, 2, 1);

            // then
            assertThat(logOverviews1.logOverviews().size()).isEqualTo(1);
            assertThat(logOverviews1.hasNext()).isTrue();
            assertThat(logOverviews2.logOverviews().size()).isEqualTo(1);
            assertThat(logOverviews2.hasNext()).isFalse();
            assertThat(logOverviews3.logOverviews().size()).isEqualTo(0);
            assertThat(logOverviews3.hasNext()).isFalse();
        }
    }
}