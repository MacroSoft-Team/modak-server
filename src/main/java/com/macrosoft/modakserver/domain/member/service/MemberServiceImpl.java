package com.macrosoft.modakserver.domain.member.service;

import com.macrosoft.modakserver.domain.member.dto.MemberResponse;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse.MemberAvatar;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse.MemberNickname;
import com.macrosoft.modakserver.domain.member.dto.MemberResponse.MemberNicknameAvatar;
import com.macrosoft.modakserver.domain.member.entity.Avatar;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.exception.MemberErrorCode;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import com.macrosoft.modakserver.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public List<MemberNickname> getNicknames(List<Long> memberIds) {
        List<Member> members = memberRepository.findAllById(memberIds);
        validateMemberNotFound(members, memberIds);
        return members.stream()
                .map(member -> new MemberResponse.MemberNickname(member.getId(), member.getNickname()))
                .toList();
    }

    private void validateMemberNotFound(List<Member> members, List<Long> memberIds) {
        if (members.size() != memberIds.size()) {
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public MemberNickname updateNickname(Member member, String nickname) {
        nickname = nickname.trim();
        validateNickname(member, nickname);
        member.setNickname(nickname);
        memberRepository.save(member);
        return new MemberNickname(member.getId(), member.getNickname());
    }

    @Override
    public List<MemberNicknameAvatar> getNicknamesAndAvatars(List<Long> memberIds) {
        List<Member> members = memberRepository.findAllById(memberIds);
        validateMemberNotFound(members, memberIds);
        return members.stream()
                .map(this::getMemberNicknameAvatar)
                .toList();
    }

    @Override
    @Transactional
    public MemberAvatar updateAvatar(Member member, int hatType, int faceType, int topType) {
        validateAvatarType(hatType, faceType, topType);
        Avatar avatar = member.getAvatar();
        avatar.setHatType(hatType);
        avatar.setFaceType(faceType);
        avatar.setTopType(topType);
        memberRepository.save(member);
        return new MemberAvatar(hatType, faceType, topType);
    }

    private void validateAvatarType(int hatType, int faceType, int topType) {
        if (hatType < 0) {
            throw new CustomException(MemberErrorCode.MEMBER_AVATAR_TYPE_INVALID);
        }
        if (faceType < 0) {
            throw new CustomException(MemberErrorCode.MEMBER_AVATAR_TYPE_INVALID);
        }
        if (topType < 0) {
            throw new CustomException(MemberErrorCode.MEMBER_AVATAR_TYPE_INVALID);
        }
    }


    private MemberNicknameAvatar getMemberNicknameAvatar(Member member) {
        Avatar avatar = member.getAvatar();
        return new MemberNicknameAvatar(
                member.getId(),
                member.getNickname(),
                new MemberAvatar(
                        avatar.getHatType(),
                        avatar.getFaceType(),
                        avatar.getTopType()
                )
        );
    }

    private void validateNickname(Member member, String newNickname) {
        if (newNickname == null || newNickname.isBlank()) {
            throw new CustomException(MemberErrorCode.MEMBER_NICKNAME_EMPTY);
        }
        if (newNickname.length() < 2) {
            throw new CustomException(MemberErrorCode.MEMBER_NICKNAME_TOO_SHORT);
        }
        if (newNickname.length() > 15) {
            throw new CustomException(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG);
        }
        if (member.getNickname().equals(newNickname)) {
            throw new CustomException(MemberErrorCode.MEMBER_NICKNAME_SAME);
        }
    }

    @Override
    public Member getMemberInDB(Member member) {
        return memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
