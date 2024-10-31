package com.macrosoft.modakserver.domain.log.service;

import com.macrosoft.modakserver.domain.log.dto.LogRequest.PrivateLogInfo;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.PrivateLogInfos;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogIds;
import com.macrosoft.modakserver.domain.log.entity.Location;
import com.macrosoft.modakserver.domain.log.entity.PrivateLog;
import com.macrosoft.modakserver.domain.log.repository.PrivateLogRepository;
import com.macrosoft.modakserver.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final PrivateLogRepository privateLogRepository;

    @Override
    public LogIds uploadPrivateLog(Member member, PrivateLogInfos privateLogInfoList) {
        List<Long> longList = new ArrayList<>();

        for (PrivateLogInfo logInfo : privateLogInfoList.privateLogInfos()) {
            // PrivateLog 엔티티 생성
            PrivateLog privateLog = PrivateLog.builder()
                    .member(member)
                    .startAt(logInfo.startAt())
                    .endAt(logInfo.endAt())
                    .location(Location.builder()
                            .minLatitude(logInfo.minLatitude())
                            .maxLatitude(logInfo.maxLatitude())
                            .minLongitude(logInfo.minLongitude())
                            .maxLongitude(logInfo.maxLongitude())
                            .address(logInfo.address())
                            .build()
                    )
                    .build();

            PrivateLog savedLog = privateLogRepository.save(privateLog);
            longList.add(savedLog.getId());
        }

        return new LogIds(longList);
    }
}
