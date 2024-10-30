package com.macrosoft.modakserver.domain.campfire.repository;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampfireRepository extends JpaRepository<Campfire, Long> {
}
