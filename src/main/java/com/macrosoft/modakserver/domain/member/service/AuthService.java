package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.entity.SocialType;

public interface AuthService {
    MemberResponse.MemberLogin login(SocialType socialType, String authorizationCode, String identityToken, String encryptedUserIdentifier);
}