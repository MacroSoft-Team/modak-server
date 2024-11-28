package com.macrosoft.modakserver.domain.admin.service;

import com.macrosoft.modakserver.domain.admin.entity.StatEntityType;
import com.macrosoft.modakserver.domain.admin.entity.Statistics;
import com.macrosoft.modakserver.domain.admin.repository.StatisticsRepository;
import com.macrosoft.modakserver.domain.campfire.repository.CampfireRepository;
import com.macrosoft.modakserver.domain.log.repository.EmotionRepository;
import com.macrosoft.modakserver.domain.log.repository.LogImageRepository;
import com.macrosoft.modakserver.domain.log.repository.LogRepository;
import com.macrosoft.modakserver.domain.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public void saveStatics() {
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        log.info("{} 통계 저장 실행", date);
        Long totalEmotionCount = emotionRepository.count();
        Long totalCampfireCount = campfireRepository.count();
        Long totalActiveCampfireCount = 0L;
        Long totalLogCount = logRepository.count();
        Long totalImageCount = logImageRepository.count();
        Long totalMemberCount = memberRepository.count();
        Statistics statistics = Statistics.builder()
                .date(date)
                .emotionCount(totalEmotionCount)
                .campfireCount(totalCampfireCount)
                .activeCampfireCount(totalActiveCampfireCount)
                .logCount(totalLogCount)
                .imageCount(totalImageCount)
                .memberCount(totalMemberCount)
                .build();
        statistics = statisticsRepository.save(statistics);
        log.info("{} 통계 저장 완료.\n{}", date, statistics);
    }

    @Override
    public Map<StatEntityType, Map<LocalDate, Long>> getStatistics() {
        Map<StatEntityType, Map<LocalDate, Long>> statistics = new HashMap<>();
        List<Statistics> statisticsList = statisticsRepository.findAll();
        return Map.of();
    }
}
