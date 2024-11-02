package com.macrosoft.modakserver.domain.campfire.service;

import static java.util.stream.Collectors.toSet;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfo;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfirePin;
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
import java.util.Set;
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
    public CampfirePin createCampfire(Member member, String campfireName) {
        validateCampfireName(campfireName);

        int pin = generateUniquePin();
        Campfire campfire = Campfire.builder()
                .pin(pin)
                .name(campfireName)
                .build();
        campfire = campfireRepository.save(campfire);

        addMemberToCampfire(member, campfire);
        log.info("모닥불 생성됨: {}", campfire.getName());
        return new CampfirePin(campfire.getPin());
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

    private void addMemberToCampfire(Member member, Campfire campfire) {
        MemberCampfire memberCampfire = new MemberCampfire();

        campfire.addMemberCampfire(memberCampfire);
        member.addMemberCampfire(memberCampfire);

        memberCampfireRepository.save(memberCampfire);
    }

    @Override
    public CampfireResponse.CampfireInfos getMyCampfires(Member member) {
        List<MemberCampfire> memberCampfires = memberCampfireRepository.findAllByMember(member);
        List<CampfireResponse.CampfireInfo> campfireInfos = new ArrayList<>();
        for (MemberCampfire memberCampfire : memberCampfires) {
            Campfire campfire = memberCampfire.getCampfire();
            campfireInfos.add(new CampfireInfo(
                    campfire.getPin(),
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
    public CampfireResponse.CampfireMain getCampfireMain(int campfirePin) {
        Campfire campfire = findCampfireByPin(campfirePin);
        return new CampfireResponse.CampfireMain(
                campfire.getPin(),
                campfire.getName(),
                null, // TODO: 오늘의 이미지
                campfire.getMemberCampfires().stream()
                        .map(MemberCampfire::getMember)
                        .map(Member::getId)
                        .collect(toSet())
        );
    }

    @Override
    public CampfireResponse.CampfireName getCampfireName(int campfirePin) {
        Campfire campfire = findCampfireByPin(campfirePin);
        return new CampfireResponse.CampfireName(campfire.getPin(), campfire.getName());
    }

    @Override
    @Transactional
    public CampfirePin joinCampfire(Member member, int campfirePin, String campfireName) {
        Campfire campfire = findCampfireByPin(campfirePin);
        if (isNameNotMatch(campfireName, campfire)) {
            throw new CustomException(CampfireErrorCode.CAMPFIRE_NAME_NOT_MATCH);
        }

        addMemberToCampfire(member, campfire);
        return new CampfirePin(campfire.getPin());
    }

    private boolean isNameNotMatch(String campfireName, Campfire campfire) {
        return !campfire.getName().equals(campfireName);
    }

    @Override
    @Transactional
    public CampfireResponse.CampfireName updateCampfireName(Member member, int campfirePin, String newCampfireName) {
        validateCampfireName(newCampfireName);
        Campfire campfire = findCampfireByPin(campfirePin);
        validateMemberInCampfire(member, campfire);
        campfire.setName(newCampfireName);
        return new CampfireResponse.CampfireName(campfire.getPin(), campfire.getName());
    }

    @Override
    @Transactional
    public void deleteCampfire(Member member, int campfirePin) {
        Campfire campfire = findCampfireByPin(campfirePin);
        validateMemberInCampfire(member, campfire);

        // 연관 관계 삭제
        Set<Member> members = campfire.getMemberCampfires().stream()
                .map(MemberCampfire::getMember)
                .collect(toSet());
        for (Member m : members) {
            deleteMemberFromCampfire(m, campfire);
        }

        campfireRepository.delete(campfire);
    }

    private void deleteMemberFromCampfire(Member member, Campfire campfire) {
        MemberCampfire memberCampfire = memberCampfireRepository.findByMemberAndCampfire(member, campfire)
                .orElseThrow(() -> new CustomException(CampfireErrorCode.REMOVE_FROM_CAMPFIRE_MEMBER_NOT_IN_CAMPFIRE));

        campfire.removeMemberCampfire(memberCampfire);
        member.removeMemberCampfire(memberCampfire);
        memberCampfireRepository.delete(memberCampfire);
    }

    private Campfire findCampfireByPin(int pin) {
        return campfireRepository.findByPin(pin)
                .orElseThrow(() -> new CustomException(CampfireErrorCode.CAMPFIRE_NOT_FOUND_BY_PIN));
    }

    private void validateMemberInCampfire(Member member, Campfire campfire) {
        if (campfire.getMemberCampfires().stream()
                .map(MemberCampfire::getMember)
                .noneMatch(m -> m.equals(member))) {
            throw new CustomException(CampfireErrorCode.MEMBER_NOT_IN_CAMPFIRE);
        }
    }
}
