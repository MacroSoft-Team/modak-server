package com.macrosoft.modakserver.domain.log.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LogRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrivateLogInfo {
        private String address;
        private Double minLatitude;
        private Double maxLatitude;
        private Double minLongitude;
        private Double maxLongitude;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrivateLogInfos {
        private List<PrivateLogInfo> privateLogInfos;
    }
}
