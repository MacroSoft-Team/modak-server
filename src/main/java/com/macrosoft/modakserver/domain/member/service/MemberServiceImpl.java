package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse.MemberNickname;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public MemberNickname getMyNickname(Member member) {
        return new MemberResponse.MemberNickname(member.getId(), member.getNickname());
    }

    @Override
    public List<MemberNickname> getNicknames(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds).stream()
                .map(member -> new MemberResponse.MemberNickname(member.getId(), member.getNickname()))
                .toList();
    }

    @Override
    @Transactional
    public MemberNickname updateNickname(Member member, String nickname) {
        member.setNickname(nickname);
        memberRepository.save(member);
        return new MemberNickname(member.getId(), member.getNickname());
    }
}
