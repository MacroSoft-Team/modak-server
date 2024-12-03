package com.macrosoft.modakserver.domain.campfire.repository;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CampfireRepository extends JpaRepository<Campfire, Long> {
    boolean existsByPin(int pin);

    Optional<Campfire> findByPin(int pin);

    @Transactional
    @Modifying
    @Query("UPDATE Campfire c SET c.pin = :pin WHERE c.id = :id")
    void updateCampfirePinById(@Param("pin") int pin, @Param("id") Long id);
}
