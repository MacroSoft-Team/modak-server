package com.macrosoft.modakserver.domain.member.repository;

import com.macrosoft.modakserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByClientId(String clientId);
}
