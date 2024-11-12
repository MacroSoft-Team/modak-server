package com.macrosoft.modakserver.domain.campfire.service;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageDTO;
import com.macrosoft.modakserver.domain.member.entity.Member;

public interface CampfireService {
    CampfireResponse.CampfirePin createCampfire(Member member, String campfireName);

    CampfireResponse.CampfireInfos getMyCampfires(Member member);

    CampfireResponse.CampfireMain getCampfireMain(Member member, int campfirePin);

    CampfireResponse.CampfireName getCampfireName(Member member, int campfirePin);

    CampfireResponse.CampfirePin joinCampfire(Member member, int campfirePin, String campfireName);

    CampfireResponse.CampfireName updateCampfireName(Member member, int campfirePin, String newCampfireName);

    CampfireResponse.CampfirePin leaveCampfire(Member member, int campfirePin);

    CampfireResponse.CampfirePin deleteCampfire(Member member, int campfirePin);

    Campfire findCampfireByPin(int campfirePin);

    void validateMemberInCampfire(Member member, Campfire campfire);

    ImageDTO getTodayImageDTO(Campfire campfire);
}