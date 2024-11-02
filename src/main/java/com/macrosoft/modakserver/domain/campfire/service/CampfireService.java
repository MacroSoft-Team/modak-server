package com.macrosoft.modakserver.domain.campfire.service;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;

public interface CampfireService {
    CampfireResponse.CampfirePin createCampfire(Member member, String campfireName);

    CampfireResponse.CampfireInfos getMyCampfires(Member member);

    CampfireResponse.CampfireMain getCampfireMain(int campfirePin);

    CampfireResponse.CampfireName getCampfireName(int campfirePin);

    CampfireResponse.CampfirePin joinCampfire(Member member, int campfirePin, String campfireName);

    CampfireResponse.CampfireName updateCampfireName(int campfirePin, String newCampfireName);

    CampfireResponse.CampfirePin deleteCampfire(int campfirePin);
}