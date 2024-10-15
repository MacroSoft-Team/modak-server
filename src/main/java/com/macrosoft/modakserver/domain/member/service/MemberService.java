package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.SocialType;

public interface MemberService {
    MemberResponse.MemberInfo getMemberInfo(Member member);
    MemberResponse.MemberInfo updateNickname(Member member, String nickname);
}
