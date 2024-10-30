package com.macrosoft.modakserver.domain.campfire.service;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;

public interface CampfireService {
    CampfireResponse.CampfireId createCampfire(String campfireName);

    CampfireResponse.CampfireInfos getMyCampfires();

    CampfireResponse.CampfireMain getCampfireMain(int campfireId);

    CampfireResponse.CampfireName getCampfireInvitations(int campfireId);

    CampfireResponse.CampfireId joinCampfire(Member member, int campfireId, String campfireName);

    CampfireResponse.CampfireName updateCampfireName(int campfireId, String newCampfireName);

    CampfireResponse.CampfireId deleteCampfire(int campfireId);
}