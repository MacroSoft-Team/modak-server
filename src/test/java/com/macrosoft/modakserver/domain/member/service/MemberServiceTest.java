package com.macrosoft.modakserver.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .clientId("clientId")
                .socialType(SocialType.APPLE)
                .nickname("nickname")
                .permissionRole(PermissionRole.CLIENT)
                .build()
        );
    }

    @Nested
    class getMemberNicknameTests {
        @Test
        void 이름_가져오기_성공_반환값_검사() {
            // when
            MemberResponse.MemberNickname memberNickname = memberService.getMemberNickname(member);

            // then
            assertThat(memberNickname.getMemberId()).isEqualTo(member.getId());
            assertThat(memberNickname.getNickname()).isEqualTo(member.getNickname());
        }
    }

    @Nested
    class updateNicknameTests {
        @Test
        void 이름_변경_성공_반환값_검사() {
            // given
            String nickname = "new nickname";

            // when
            MemberResponse.MemberNickname memberNickname = memberService.updateNickname(member, nickname);

            // then
            assertThat(memberNickname.getMemberId()).isEqualTo(member.getId());
            assertThat(memberNickname.getNickname()).isEqualTo(nickname);
        }
    }
}