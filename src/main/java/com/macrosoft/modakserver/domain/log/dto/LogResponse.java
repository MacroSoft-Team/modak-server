package com.macrosoft.modakserver.domain.log.dto;

import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public class LogResponse {
    public record LogMetadataList(List<LogMetadata> logMetadataList) {
    }

    public record LogMetadata(
            @Schema(description = "장작 사건 시작 시간", example = "2024-11-06T01:41:01.83819")
            LocalDateTime startAt,
            @Schema(description = "장작 사건 종료 시간", example = "2024-11-06T02:21:05.43212")
            LocalDateTime endAt,
            @Schema(description = "장작 주소", example = "포항시 남구")
            String address,
            @Schema(description = "장작 최소 위도", example = "37.123456")
            Double minLatitude,
            @Schema(description = "장작 최대 위도", example = "37.654321")
            Double maxLatitude,
            @Schema(description = "장작 최소 경도", example = "127.123456")
            Double minLongitude,
            @Schema(description = "장작 최대 위도", example = "127.654321")
            Double maxLongitude
    ) {
    }

    public record LogId(Long logId) {
    }

    public record LogOverviews(
            List<LogOverview> logOverviews,
            boolean hasNext) {
    }

    public record LogOverview(
            @Schema(description = "장작 아이디", example = "1")
            Long logId,
            @Schema(description = "장작 사건 시작 시간", example = "2024-11-06T01:41:01.83819")
            LocalDateTime startAt,
            @Schema(description = "장작 주소", example = "포항시 남구")
            String address,
            @Schema(description = "장작 미리보기 사진들, 최대 8장")
            List<String> imageNames
    ) {
    }

    public record LogDetails(
            @Schema(description = "장작 아이디", example = "1")
            Long logId,
            @Schema(description = "장작 사진 이름과 아이디들")
            List<ImageName> images,
            boolean hasNext
    ) {
    }
}
