package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.entity.SocialType;
import com.macrosoft.modakserver.domain.member.exception.MemberErrorCode;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member signup(SocialType socialType, String authorizationCode, String identityToken, String hashedUserId) {
        if (memberRepository.existsByClientId(hashedUserId)) {
            throw new CustomException(MemberErrorCode.MEMBER_ALREADY_EXIST);
        }

        String nickname = makeRandomNickname();

        Member member = Member.builder()
                .clientId(hashedUserId)
                .socialType(socialType)
                .nickname(nickname)
                .build();

        return memberRepository.save(member);
    }

    private String makeRandomNickname() {
        return "nickname";
    }
}
