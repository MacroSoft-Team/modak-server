package com.macrosoft.modakserver.domain.member.repository;

import com.macrosoft.modakserver.domain.member.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
