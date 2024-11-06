package com.macrosoft.modakserver.domain.log.service;

import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.PermissionRole;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
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
}