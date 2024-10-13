package com.macrosoft.modakserver.test.service;

import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final MemberRepository memberRepository;

    @Override
    public List<Member> get() {
        return memberRepository.findAll();
    }
}
