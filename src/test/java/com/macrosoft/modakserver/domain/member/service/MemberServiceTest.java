package com.macrosoft.modakserver.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.List;
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
        void 닉네임_가져오기_성공_반환값_검사() {
            // when
            MemberResponse.MemberNickname memberNickname = memberService.getNicknames(List.of(member.getId())).get(0);

            // then
            assertThat(memberNickname.memberId()).isEqualTo(member.getId());
            assertThat(memberNickname.nickname()).isEqualTo(member.getNickname());
        }
    }

    @Nested
    class updateNicknameTests {
        @Test
        void 닉네임_변경_성공_반환값_검사() {
            // given
            String nickname = "new nickname";

            // when
            MemberResponse.MemberNickname memberNickname = memberService.updateNickname(member, nickname);

            // then
            assertThat(memberNickname.memberId()).isEqualTo(member.getId());
            assertThat(memberNickname.nickname()).isEqualTo(nickname);
        }

        @ParameterizedTest
        @ValueSource(strings = {"애플", "aa", "ACE!!", "열다섯글자의이름입니다이이이이", "aaaaaaaaaaaaaaa"})
        void 닉네임은_2자에서_15자까지_가능하다(String newNickname) {
            // when
            MemberResponse.MemberNickname memberNickname = memberService.updateNickname(member, newNickname);

            // then
            assertThat(memberNickname.nickname()).isEqualTo(newNickname);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "애", "a", "열다섯글자의이름입니다이이이이이", "aaaaaaaaa aaaaaa"})
        void 닉네임은_2자에서_15자_벗어나면_예외발생(String newNickname) {
            // when then
            assertThatThrownBy(() -> memberService.updateNickname(member, newNickname))
                    .isInstanceOf(CustomException.class);
        }

        @Test
        void 닉네임은_이전_닉네임과_같으면_안된다() {
            // given
            String nickname = member.getNickname();

            // when
            assertThatThrownBy(() -> memberService.updateNickname(member, nickname))
                    .isInstanceOf(CustomException.class);
        }
    }
}