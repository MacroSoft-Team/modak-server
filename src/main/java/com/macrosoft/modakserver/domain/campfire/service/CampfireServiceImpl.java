package com.macrosoft.modakserver.domain.campfire.service;

import static java.util.stream.Collectors.toSet;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfo;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireJoinInfo;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfirePin;
import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.domain.campfire.exception.CampfireErrorCode;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.repository.MemberCampfireRepository;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageDTO;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.domain.member.service.MemberService;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.ArrayList;
import java.util.HashSet;
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
    private final MemberService memberService;

    @Override
    @Transactional
    public CampfirePin createCampfire(Member member, String campfireName) {
        Member memberInDB = memberService.getMemberInDB(member);
        validateCampfireName(campfireName);

        int pin = generateUniquePin();
        Campfire campfire = Campfire.builder()
                .pin(pin)
                .name(campfireName)
                .build();
        campfire = campfireRepository.save(campfire);

        addMemberToCampfire(memberInDB, campfire);
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
                    getTodayImageDTO(campfire).name()
            ));
        }
        return new CampfireResponse.CampfireInfos(campfireInfos);
    }

    public ImageDTO getTodayImageDTO(Campfire campfire) {
        LogImage todayImage = campfire.getTodayImage();
        ImageDTO todayImageDTO;
        if (todayImage == null) {
            todayImageDTO = new ImageDTO(0L, "", new HashSet<>());
        } else {
            todayImageDTO = ImageDTO.of(todayImage);
        }
        return todayImageDTO;
    }

    @Override
    public CampfireJoinInfo getCampfireJoin(int campfirePin) {
        Campfire campfire = findCampfireByPin(campfirePin);
        return new CampfireJoinInfo(
                campfire.getName(),
                campfire.getCreatedAt(),
                campfire.getMemberCampfires().stream()
                        .map(MemberCampfire::getMember)
                        .map(Member::getNickname)
                        .collect(toSet())
        );
    }

    @Override
    public CampfireResponse.CampfireMain getCampfireMain(Member member, int campfirePin) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = findCampfireByPin(campfirePin);
        validateMemberInCampfire(memberInDB, campfire);

        return new CampfireResponse.CampfireMain(
                campfire.getPin(),
                campfire.getName(),
                getTodayImageDTO(campfire),
                campfire.getMemberCampfires().stream()
                        .map(MemberCampfire::getMember)
                        .map(Member::getId)
                        .collect(toSet())
        );
    }

    @Override
    public CampfireResponse.CampfireName getCampfireName(Member member, int campfirePin) {
        Campfire campfire = findCampfireByPin(campfirePin);
        Member memberInDB = memberService.getMemberInDB(member);
        validateMemberInCampfire(memberInDB, campfire);
        return new CampfireResponse.CampfireName(campfire.getPin(), campfire.getName());
    }

    @Override
    @Transactional
    public CampfirePin joinCampfire(Member member, int campfirePin, String campfireName) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = findCampfireByPin(campfirePin);
        validateMemberCampfireJoin(campfireName, campfire, memberInDB);

        addMemberToCampfire(memberInDB, campfire);
        return new CampfirePin(campfire.getPin());
    }

    private void validateMemberCampfireJoin(String campfireName, Campfire campfire, Member memberInDB) {
        if (isNameNotMatch(campfireName, campfire)) {
            throw new CustomException(CampfireErrorCode.CAMPFIRE_NAME_NOT_MATCH);
        }
        if (isMemberInCampfire(memberInDB, campfire)) {
            throw new CustomException(CampfireErrorCode.MEMBER_ALREADY_IN_CAMPFIRE);
        }
        if (isCampfireMemberFull(campfire)) {
            throw new CustomException(CampfireErrorCode.CAMPFIRE_MEMBER_FULL);
        }
    }


    private boolean isCampfireMemberFull(Campfire campfire) {
        return campfire.getMemberCampfires().size() >= 6;
    }

    private boolean isNameNotMatch(String campfireName, Campfire campfire) {
        return !campfire.getName().equals(campfireName);
    }

    @Override
    @Transactional
    public CampfireResponse.CampfireName updateCampfireName(Member member, int campfirePin, String newCampfireName) {
        Member memberInDB = memberService.getMemberInDB(member);
        validateCampfireName(newCampfireName);
        Campfire campfire = findCampfireByPin(campfirePin);
        validateMemberInCampfire(memberInDB, campfire);
        campfire.setName(newCampfireName);
        return new CampfireResponse.CampfireName(campfire.getPin(), campfire.getName());
    }

    @Override
    @Transactional
    public CampfireResponse.CampfirePin leaveCampfire(Member member, int campfirePin) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = findCampfireByPin(campfirePin);
        validateMemberInCampfire(memberInDB, campfire);

        if (isLastMember(campfire)) {
            return deleteCampfire(memberInDB, campfirePin);
        }
        deleteMemberFromCampfire(memberInDB, campfire);
        return new CampfirePin(campfirePin);
    }

    private static boolean isLastMember(Campfire campfire) {
        return campfire.getMemberCampfires().size() == 1;
    }

    @Override
    @Transactional
    public CampfireResponse.CampfirePin deleteCampfire(Member member, int campfirePin) {
        Member memberInDB = memberService.getMemberInDB(member);
        Campfire campfire = findCampfireByPin(campfirePin);
        validateMemberInCampfire(memberInDB, campfire);

        if (!isLastMember(campfire)) {
            throw new CustomException(CampfireErrorCode.NOT_LAST_MEMBER);
        }

        // 연관 관계 삭제
        Set<Member> members = campfire.getMemberCampfires().stream()
                .map(MemberCampfire::getMember)
                .collect(toSet());
        for (Member m : members) {
            deleteMemberFromCampfire(m, campfire);
        }

        campfireRepository.delete(campfire);
        return new CampfirePin(campfirePin);
    }

    private void deleteMemberFromCampfire(Member member, Campfire campfire) {
        MemberCampfire memberCampfire = memberCampfireRepository.findByMemberAndCampfire(member, campfire)
                .orElseThrow(() -> new CustomException(CampfireErrorCode.REMOVE_FROM_CAMPFIRE_MEMBER_NOT_IN_CAMPFIRE));

        campfire.removeMemberCampfire(memberCampfire);
        member.removeMemberCampfire(memberCampfire);
        memberCampfireRepository.delete(memberCampfire);
    }

    @Override
    public Campfire findCampfireByPin(int campfirePin) {
        return campfireRepository.findByPin(campfirePin)
                .orElseThrow(() -> new CustomException(CampfireErrorCode.CAMPFIRE_NOT_FOUND_BY_PIN));
    }

    @Override
    public void validateMemberInCampfire(Member member, Campfire campfire) {
        if (!isMemberInCampfire(member, campfire)) {
            throw new CustomException(CampfireErrorCode.MEMBER_NOT_IN_CAMPFIRE);
        }
    }

    private boolean isMemberInCampfire(Member member, Campfire campfire) {
        return campfire.getMemberCampfires().stream()
                .map(MemberCampfire::getMember)
                .anyMatch(m -> m.equals(member));
    }
}
