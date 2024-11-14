package com.macrosoft.modakserver.domain.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public class LogRequest {
    public record UploadLog(
            LogResponse.LogMetadata logMetadata,
            List<ImageInfo> imageInfos) {
    }

    public record ImageInfo(
            @Schema(description = "s3에 저장된 사진 이름", example = "dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String imageName,
            @Schema(description = "사진 위도", example = "127.654321")
            Double latitude,
            @Schema(description = "사진 경도", example = "127.654321")
            Double longitude,
            @Schema(description = "사진 촬영 시각", example = "2024-11-06T02:21:05.43212")
            LocalDateTime takenAt) {
    }

    public record EmotionDTO(
            @Schema(description = "감정표현", example = "😀")
            String emotion) {
    }
}
