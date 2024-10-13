package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import org.springframework.stereotype.Service;

public interface MemberService {
    Member signup(SocialType socialType, String authorizationCode, String identityToken, String hashedUserId);
}
