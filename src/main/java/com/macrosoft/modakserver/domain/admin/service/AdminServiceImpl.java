package com.macrosoft.modakserver.domain.admin.service;

import com.macrosoft.modakserver.domain.admin.entity.StatEntityType;
import com.macrosoft.modakserver.domain.admin.entity.Statistics;
import com.macrosoft.modakserver.domain.admin.repository.StatisticsRepository;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.log.entity.Emotion;
import com.macrosoft.modakserver.domain.log.repository.EmotionRepository;
import com.macrosoft.modakserver.domain.log.repository.LogImageRepository;
import com.macrosoft.modakserver.domain.log.repository.LogRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final StatisticsRepository statisticsRepository;
    private final EmotionRepository emotionRepository;
    private final CampfireRepository campfireRepository;
    private final LogRepository logRepository;
    private final LogImageRepository logImageRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void saveStatics(LocalDate date) {
        log.info("{} 통계 저장 실행", date);

        Statistics statistics = Statistics.builder()
                .date(date)
                .emotionCount(emotionRepository.count())
                .campfireCount(campfireRepository.count())
                .logCount(logRepository.count())
                .imageCount(logImageRepository.count())
                .memberCount(memberRepository.count())
                .activeMemberCount(calculateActiveMemberCount())
                .build();

        statistics = statisticsRepository.save(statistics);

        log.info("{} 통계 저장 완료.\n{}", date, statistics);
    }

    // MARK: 임시로 최근 1주일간 감정표현 누른 회원을 활동 회원으로 가정
    private Long calculateActiveMemberCount() {
        // 1. 최근 1주일간의 모든 Emote 가져오기
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        List<Emotion> recentEmotions = emotionRepository.findAllByCreatedAtGreaterThan(weekAgo);

        // 2. Emote 의 Member 가져오고 중복 제거
        Set<Member> activeMembers = recentEmotions.stream()
                .map(Emotion::getMember)
                .collect(Collectors.toSet());

        return (long) activeMembers.size();
    }

    @Override
    public Map<StatEntityType, Map<LocalDate, Long>> getStatistics() {
        List<Statistics> statisticsList = statisticsRepository.findAll();

        Map<StatEntityType, Map<LocalDate, Long>> statistics = initializeStatisticsMap();

        for (Statistics stat : statisticsList) {
            LocalDate date = stat.getDate();
            statistics.get(StatEntityType.EMOTION).put(date, stat.getEmotionCount());
            statistics.get(StatEntityType.CAMPFIRE).put(date, stat.getCampfireCount());
            statistics.get(StatEntityType.LOG).put(date, stat.getLogCount());
            statistics.get(StatEntityType.IMAGE).put(date, stat.getImageCount());
            statistics.get(StatEntityType.MEMBER).put(date, stat.getMemberCount());
            statistics.get(StatEntityType.ACTIVE_MEMBER).put(date, stat.getActiveMemberCount());
        }

        return statistics;
    }

    private Map<StatEntityType, Map<LocalDate, Long>> initializeStatisticsMap() {
        return new EnumMap<>(StatEntityType.class) {{
            put(StatEntityType.EMOTION, new HashMap<>());
            put(StatEntityType.CAMPFIRE, new HashMap<>());
            put(StatEntityType.LOG, new HashMap<>());
            put(StatEntityType.IMAGE, new HashMap<>());
            put(StatEntityType.MEMBER, new HashMap<>());
            put(StatEntityType.ACTIVE_MEMBER, new HashMap<>());
        }};
    }
}
