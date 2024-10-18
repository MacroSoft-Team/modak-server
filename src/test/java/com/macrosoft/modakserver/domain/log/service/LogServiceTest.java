package com.macrosoft.modakserver.domain.log.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.PrivateLogInfo;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.PrivateLogInfos;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.entity.Location;
import com.macrosoft.modakserver.domain.log.entity.PrivateLog;
import com.macrosoft.modakserver.domain.log.repository.PrivateLogRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
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
    private LogService logService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PrivateLogRepository privateLogRepository;
    private Member member0;

    @BeforeEach
    void setUp() {
        member0 = memberRepository.save(Member.builder()
                .clientId("clientId0")
                .socialType(SocialType.APPLE)
                .nickname("nickname0")
                .permissionRole(PermissionRole.CLIENT)
                .build()
        );
    }

    @Nested
    class uploadPrivateLogTests {
        @Test
        void 개인_장작_정보_업로드_성공_반환값_검사() {
            // given
            String address = "주소";
            Double minLatitude = 1.0;
            Double maxLatitude = 2.0;
            Double minLongitude = 3.0;
            Double maxLongitude = 4.0;
            LocalDateTime startAt = LocalDateTime.now();
            LocalDateTime endAt = LocalDateTime.now().plusMinutes(10);

            LogRequest.PrivateLogInfos privateLogInfos = PrivateLogInfos.builder()
                    .privateLogInfos(List.of(PrivateLogInfo.builder()
                            .address(address)
                            .minLatitude(minLatitude)
                            .maxLatitude(maxLatitude)
                            .minLongitude(minLongitude)
                            .maxLongitude(maxLongitude)
                            .startAt(startAt)
                            .endAt(endAt)
                            .build()))
                    .build();

            // when
            LogResponse.LogIds logIds = logService.uploadPrivateLog(member0, privateLogInfos);

            // then
            Location expectedLocation = Location.builder()
                    .address(address)
                    .minLatitude(minLatitude)
                    .maxLatitude(maxLatitude)
                    .minLongitude(minLongitude)
                    .maxLongitude(maxLongitude)
                    .build();

            assertThat(logIds.getLogIds()).isNotEmpty();
            PrivateLog privateLog = privateLogRepository.findById(logIds.getLogIds().get(0)).orElseThrow();
            assertThat(privateLog.getLocation()).usingRecursiveComparison()
                    .ignoringFields("id", "createdAt", "updatedAt")
                    .isEqualTo(expectedLocation);
            assertThat(privateLog.getStartAt()).isEqualTo(startAt);
            assertThat(privateLog.getEndAt()).isEqualTo(endAt);
            assertThat(privateLog.getMember()).usingRecursiveComparison()
                    .ignoringFields("id", "createdAt", "updatedAt")
                    .isEqualTo(member0);
        }
    }
}