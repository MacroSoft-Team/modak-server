package com.macrosoft.modakserver.domain.campfire.service;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireId;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfos;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireMain;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireName;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampfireServiceImpl implements CampfireService {
    private final CampfireRepository campfireRepository;

    @Override
    public CampfireId createCampfire(String campfireName) {
        return null;
    }

    @Override
    public CampfireInfos getMyCampfires() {
        return null;
    }

    @Override
    public CampfireMain getCampfireMain(int campfireId) {
        return null;
    }

    @Override
    public CampfireName getCampfireInvitations(int campfireId) {
        return null;
    }

    @Override
    public CampfireId joinCampfire(Member member, int campfireId, String campfireName) {
        return null;
    }

    @Override
    public CampfireName updateCampfireName(int campfireId, String newCampfireName) {
        return null;
    }

    @Override
    public CampfireId deleteCampfire(int campfireId) {
        return null;
    }
}
