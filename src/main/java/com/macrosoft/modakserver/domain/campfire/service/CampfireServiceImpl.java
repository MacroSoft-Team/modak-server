package com.macrosoft.modakserver.domain.campfire.service;

import static java.util.stream.Collectors.toSet;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireId;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfo;
import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.domain.campfire.exception.CampfireErrorCode;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.repository.MemberCampfireRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
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
    public CampfireResponse.CampfireId createCampfire(Member member, String campfireName) {
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
    public CampfireResponse.CampfireInfos getMyCampfires(Member member) {
        List<MemberCampfire> memberCampfires = memberCampfireRepository.findAllByMember(member);
        List<CampfireResponse.CampfireInfo> campfireInfos = new ArrayList<>();
        for (MemberCampfire memberCampfire : memberCampfires) {
            Campfire campfire = memberCampfire.getCampfire();
            campfireInfos.add(new CampfireInfo(
                    campfire.getId(),
                    campfire.getName(),
                    campfire.getMemberCampfires().stream()
                            .map(MemberCampfire::getMember)
                            .map(Member::getNickname)
                            .collect(toSet()),
                    "" // TODO: 오늘의 이미지 이름
            ));
        }
        return new CampfireResponse.CampfireInfos(campfireInfos);
    }

    @Override
    public CampfireResponse.CampfireMain getCampfireMain(int campfireId) {
        return null;
    }

    @Override
    public CampfireResponse.CampfireName getCampfireInvitations(int campfireId) {
        return null;
    }

    @Override
    public CampfireResponse.CampfireId joinCampfire(Member member, int campfireId, String campfireName) {
        return null;
    }

    @Override
    public CampfireResponse.CampfireName updateCampfireName(int campfireId, String newCampfireName) {
        return null;
    }

    @Override
    public CampfireResponse.CampfireId deleteCampfire(int campfireId) {
        return null;
    }
}
