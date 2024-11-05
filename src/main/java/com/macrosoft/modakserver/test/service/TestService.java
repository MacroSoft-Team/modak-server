package com.macrosoft.modakserver.test.service;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import java.util.List;

public interface TestService {
    List<Member> get();

    CampfireResponse.CampfireMain addMockData(Member member);
}
