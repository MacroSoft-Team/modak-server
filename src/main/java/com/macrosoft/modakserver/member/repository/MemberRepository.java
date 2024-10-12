package com.macrosoft.modakserver.member.repository;

import com.macrosoft.modakserver.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
