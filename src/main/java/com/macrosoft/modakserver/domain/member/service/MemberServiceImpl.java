package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse.MemberNickname;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.exception.MemberErrorCode;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public List<MemberNickname> getNicknames(List<Long> memberIds) {
        List<Member> members = memberRepository.findAllById(memberIds);
        validateMemberNotFound(members, memberIds);
        return members.stream()
                .map(member -> new MemberResponse.MemberNickname(member.getId(), member.getNickname()))
                .toList();
    }

    private void validateMemberNotFound(List<Member> members, List<Long> memberIds) {
        if (members.size() != memberIds.size()) {
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public MemberNickname updateNickname(Member member, String nickname) {
        member.setNickname(nickname);
        memberRepository.save(member);
        return new MemberNickname(member.getId(), member.getNickname());
    }
}
