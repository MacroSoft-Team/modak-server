package com.macrosoft.modakserver.domain.member.repository;

import com.macrosoft.modakserver.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByClientId(String clientId);
}
