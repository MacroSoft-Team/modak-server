package com.macrosoft.modakserver.domain.campfire.service;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireId;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfos;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireMain;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireName;
import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.domain.campfire.exception.CampfireErrorCode;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.repository.MemberCampfireRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampfireServiceImpl implements CampfireService {
    private final CampfireRepository campfireRepository;
    private final MemberCampfireRepository memberCampfireRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public CampfireId createCampfire(Member member, String campfireName) {
        validateCampfireName(campfireName);

        int pin = generateUniquePin();
        Campfire campfire = Campfire.builder()
                .pin(pin)
                .name(campfireName)
                .build();
        campfire = campfireRepository.save(campfire);

        MemberCampfire memberCampfire = new MemberCampfire();

        campfire.addMemberCampfire(memberCampfire);
        member.addMemberCampfire(memberCampfire);

        memberCampfireRepository.save(memberCampfire);
        log.info("모닥불 생성됨: {}", campfire.getName());
        return new CampfireId(campfire.getId());
    }

    private void validateCampfireName(String campfireName) {
        if (campfireName == null || campfireName.isBlank()) {
            throw new CustomException(CampfireErrorCode.CAMPFIRE_NAME_EMPTY);
        }
        if (campfireName.length() > 12) {
            throw new CustomException(CampfireErrorCode.CAMPFIRE_NAME_TOO_LONG);
        }
    }

    private int generateUniquePin() {
        int randomPin;
        do {
            randomPin = generateRandomPin();
        } while (isPinExist(randomPin));
        return randomPin;
    }

    private int generateRandomPin() {
        return 100000 + new Random().nextInt(900000);
    }

    private boolean isPinExist(int randomPin) {
        return campfireRepository.existsByPin(randomPin);
    }

    @Override
    public CampfireInfos getMyCampfires(Member member) {
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
