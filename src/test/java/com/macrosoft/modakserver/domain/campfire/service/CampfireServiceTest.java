package com.macrosoft.modakserver.domain.campfire.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireId;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfo;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfos;
import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.repository.MemberCampfireRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CampfireServiceTest {
    @Autowired
    private CampfireService campfireService;

    @Autowired
    private CampfireRepository campfireRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCampfireRepository memberCampfireRepository;

    private List<Member> members;

    @BeforeEach
    void setUp() {
        members = List.of(
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

    @Nested
    class createCampfireTests {
        @Test
        void 캠프파이어_생성() {
            // given
            String campfireName = "campfireName";
            Member member = members.get(0);

            // when
            CampfireId campfireId = campfireService.createCampfire(member, campfireName);

            // then
            Optional<Campfire> campfire = campfireRepository.findById(campfireId.campfireId());
            assertThat(campfire).isPresent();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "A",
                "campfireName",
                "My CampFire",
                "캠프파이어이름은열두자임",
                "캠프파이어이름 열두자임"
        })
        void 캠프파이어의_이름은_12자까지_가능하다_성공(String campfireName) {
            // given
            Member member = members.get(0);

            // when
            CampfireId campfireId = campfireService.createCampfire(member, campfireName);

            // then
            Campfire campfire = campfireRepository.findById(campfireId.campfireId()).orElseThrow();
            assertThat(campfire.getName()).isEqualTo(campfireName);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "campfireNames",
                "캠프파이어이름열세자되버림",
                "캠프파이어 이름열세자가되"
        })
        void 캠프파이어의_이름은_12자까지_가능하다_실패(String campfireName) {
            // given
            Member member = members.get(0);

            // when, then
            assertThatThrownBy(() ->
                    campfireService.createCampfire(member, campfireName)
            ).isInstanceOf(CustomException.class);
        }

        @Test
        void 캠프파이어의_핀은_6자리_정수이다() {
            // given
            String campfireName = "campfireName";
            Member member = members.get(0);

            // when
            CampfireId campfireId = campfireService.createCampfire(member, campfireName);

            // then
            Campfire campfire = campfireRepository.findById(campfireId.campfireId()).orElseThrow();
            assertThat(campfire.getPin()).isBetween(100000, 999999);
        }

        @Test
        void 캠프파이어에_생성한_멤버가_소속한다() {
            // given
            String campfireName = "campfireName";
            Member member = members.get(0);

            // when
            Long campfireId = campfireService.createCampfire(member, campfireName).campfireId();

            // then
            MemberCampfire dbMemberCampfire = memberCampfireRepository.findAllByMember(member).get(0);
            assertThat(dbMemberCampfire.getMember()).isEqualTo(member);
            assertThat(dbMemberCampfire.getCampfire().getId()).isEqualTo(campfireId);
            Campfire dbCampfire = campfireRepository.findById(campfireId).orElseThrow();
            assertThat(dbCampfire.getMemberCampfires().stream()
                    .map(MemberCampfire::getMember)
                    .toList()).contains(member);
        }
    }

    @Nested
    class getMyCampfiresTests {
        @Test
        void getMyCampfires() {
            String campfireName1 = "campfire1";
            String campfireName2 = "campfire2";
            Member member = members.get(0);
            campfireService.createCampfire(member, campfireName1);
            campfireService.createCampfire(member, campfireName2);

            // when
            CampfireInfos campfireInfos = campfireService.getMyCampfires(member);

            // then
            assertThat(campfireInfos.campfireInfos().size()).isEqualTo(2);
            assertThat(campfireInfos.campfireInfos().stream()
                    .map(CampfireInfo::campfireName)
                    .toList()).containsExactlyInAnyOrder(campfireName1, campfireName2);
            assertThat(campfireInfos.campfireInfos().stream()
                    .map(CampfireInfo::membersNames)
                    .collect(Collectors.toSet())).containsExactlyInAnyOrder(Set.of(member.getNickname()));
        }
    }

    @Nested
    class getCampfireMainTests {
        @Test
        void getCampfireMain() {

        }
    }

    @Nested
    class getCampfireInvitationsTests {
        @Test
        void getCampfireInvitations() {

        }
    }

    @Nested
    class joinCampfireTests {
        @Test
        void joinCampfire() {

        }
    }

    @Nested
    class updateCampfireNameTests {
        @Test
        void updateCampfireName() {

        }
    }

    @Nested
    class deleteCampfireTests {
        @Test
        void deleteCampfire() {

        }
    }
}