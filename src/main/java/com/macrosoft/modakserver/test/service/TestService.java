package com.macrosoft.modakserver.test.service;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;

public interface TestService {
    CampfireResponse.CampfireMain addMockData(Member member);
}
