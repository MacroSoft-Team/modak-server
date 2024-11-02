package com.macrosoft.modakserver.domain.log.dto;

import java.time.LocalDateTime;
import java.util.List;

public class LogRequest {
    public record PrivateLogInfo(
            String address,
            Double minLatitude,
            Double maxLatitude,
            Double minLongitude,
            Double maxLongitude,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
    }

    public record PrivateLogInfos(List<PrivateLogInfo> privateLogInfos) {
    }
}
