package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import java.util.List;

public interface MemberService {
    List<MemberResponse.MemberNickname> getNicknames(List<Long> memberIds);

    MemberResponse.MemberNickname updateNickname(Member member, String nickname);

    List<MemberResponse.MemberNicknameAvatar> getNicknamesAndAvatars(List<Long> memberIds);

    Member getMemberInDB(Member member);
}
