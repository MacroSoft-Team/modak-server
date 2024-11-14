package com.macrosoft.modakserver.domain.log.repository;

import com.macrosoft.modakserver.domain.log.entity.Emotion;
import com.macrosoft.modakserver.domain.log.entity.LogImage;
import com.macrosoft.modakserver.domain.member.entity.Member;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    Set<Emotion> findAllByMemberAndLogImage(Member member, LogImage logImage);

    Optional<Emotion> findByMemberAndLogImage(Member member, LogImage logImage);
}
