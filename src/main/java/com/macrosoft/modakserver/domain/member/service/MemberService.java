package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse.MemberNickname;
import com.macrosoft.modakserver.domain.member.entity.Member;

public interface MemberService {
    MemberNickname getMemberNickname(Member member);
    MemberNickname updateNickname(Member member, String nickname);
}
