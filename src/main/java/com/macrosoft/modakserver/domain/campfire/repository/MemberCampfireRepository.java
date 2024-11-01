package com.macrosoft.modakserver.domain.campfire.repository;

import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCampfireRepository extends JpaRepository<MemberCampfire, Long> {
    List<MemberCampfire> findAllByMember(Member member);
}
