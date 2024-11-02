package com.macrosoft.modakserver.domain.campfire.repository;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampfireRepository extends JpaRepository<Campfire, Long> {
    boolean existsByPin(int pin);

    Optional<Campfire> findByPin(int pin);
}
