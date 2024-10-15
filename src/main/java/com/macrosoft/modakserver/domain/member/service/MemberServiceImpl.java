package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse.MemberInfo;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberInfo getMemberInfo(Member member) {
        return MemberInfo.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();
    }

    @Override
    @Transactional
    public MemberInfo updateNickname(Member member, String nickname) {
        member.setNickname(nickname);
        memberRepository.save(member);
        return MemberInfo.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();
    }
}
