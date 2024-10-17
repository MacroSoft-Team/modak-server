package com.macrosoft.modakserver.domain.log.service;

import com.macrosoft.modakserver.domain.log.dto.LogRequest.PrivateLogInfo;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.PrivateLogInfoList;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogId;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.logIdList;
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
    public logIdList uploadPrivateLog(Member member, PrivateLogInfoList privateLogInfoList) {
        log.info("uploadPrivateLog");
        List<LogId> logIdList = new ArrayList<>();

        for (PrivateLogInfo logInfo : privateLogInfoList.getPrivateLogInfoList()) {
            // PrivateLog 엔티티 생성
            PrivateLog privateLog = PrivateLog.builder()
                    .member(member)
                    .startAt(logInfo.getStartAt())
                    .endAt(logInfo.getEndAt())
                    .location(Location.builder()
                            .minLatitude(logInfo.getMinLatitude())
                            .maxLatitude(logInfo.getMaxLatitude())
                            .minLongitude(logInfo.getMinLongitude())
                            .maxLongitude(logInfo.getMaxLongitude())
                            .address(logInfo.getAddress())
                            .build()
                    )
                    .build();

            PrivateLog savedLog = privateLogRepository.save(privateLog);
            logIdList.add(LogId.builder()
                    .logId(savedLog.getId())
                    .build());
        }

        return LogResponse.logIdList.builder()
                .logIdList(logIdList)
                .build();
    }
}
