package com.macrosoft.modakserver.test.service;

import static java.util.stream.Collectors.toSet;

import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfireInfo;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfirePin;
import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.domain.campfire.exception.CampfireErrorCode;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.exception.MemberErrorCode;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.domain.member.service.AuthService;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final MemberRepository memberRepository;
    private final CampfireRepository campfireRepository;
    private final CampfireService campfireService;
    private final AuthService authService;

    @Override
    public List<Member> get() {
        return memberRepository.findAll();
    }

    @Override
    public CampfireResponse.CampfireMain addMockData(Member member) {
        String[] memberNicknameAry = member.getNickname().split(" ");
        String memberNickname;
        if (memberNicknameAry.length > 1) {
            memberNickname = memberNicknameAry[1];
        } else {
            memberNickname = memberNicknameAry[0];
        }
        String newCampfireName = memberNickname + "의 모닥불";
        int count = (int) campfireService.getMyCampfires(member).campfireInfos().stream()
                .map(CampfireInfo::campfireName)
                .filter(name -> name.contains(memberNickname))
                .count();
        newCampfireName += count;
        CampfirePin campfirePin = campfireService.createCampfire(member, newCampfireName);
        int pin = campfirePin.campfirePin();

        for (int i = 0; i < 4; i++) {
            String authorizationCode = String.valueOf(UUID.randomUUID());
            String identityToken = String.valueOf(UUID.randomUUID());
            String encryptedUserIdentifier = String.valueOf(UUID.randomUUID());
            Long newMemberId = authService.login(SocialType.APPLE, authorizationCode, identityToken,
                    encryptedUserIdentifier).memberId();
            Member newMember = memberRepository.findById(newMemberId).orElseThrow(() -> new CustomException(
                    MemberErrorCode.MEMBER_NOT_FOUND));
            campfireService.joinCampfire(newMember, pin, newCampfireName);
        }

        Campfire campfire = campfireRepository.findByPin(pin)
                .orElseThrow(() -> new CustomException(CampfireErrorCode.CAMPFIRE_NOT_FOUND_BY_PIN));

        return new CampfireResponse.CampfireMain(
                pin,
                newCampfireName,
                null,
                campfire.getMemberCampfires().stream()
                        .map(MemberCampfire::getMember)
                        .map(Member::getId)
                        .collect(toSet())
        );
    }
}
