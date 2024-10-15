package com.macrosoft.modakserver.domain.member.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class NicknameGeneratorTest {
    @Test
    void 닉네임_생성_테스트() {
        String nickname = NicknameGenerator.generateRandomNickname();
        assertThat(nickname).hasSizeGreaterThan(2);
        assertThat(nickname).contains(" ");
    }
}