package com.macrosoft.modakserver.test.service;

import com.macrosoft.modakserver.member.model.Member;
import com.macrosoft.modakserver.member.model.SocialType;
import com.macrosoft.modakserver.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final MemberRepository memberRepository;

    @Override
    public List<Member> get() {
        return memberRepository.findAll();
    }
}
