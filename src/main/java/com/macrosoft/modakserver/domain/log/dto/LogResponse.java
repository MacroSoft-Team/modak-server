package com.macrosoft.modakserver.domain.log.dto;

import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageDTO;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageEmotionDTO;
import com.macrosoft.modakserver.domain.image.entity.Emotion;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.log.entity.Location;
import com.macrosoft.modakserver.domain.log.entity.Log;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            List<LogOverview> logs,
            boolean hasNext) {
    }

    public record LogOverview(
            @Schema(description = "장작 아이디", example = "1")
            Long id,
            @Schema(description = "장작 사건 시작 시간", example = "2024-11-06T01:41:01.83819")
            LocalDateTime startAt,
            @Schema(description = "장작 주소", example = "포항시 남구")
            String address,
            @Schema(description = "장작 미리보기 사진들, 최대 8장")
            List<ImageResponse.ImageName> imageNames
    ) {
    }

    public record LogDetail(
            Long id,
            List<ImageResponse.ImageDTO> Images,
            LogMetadata logMetadata
    ) {
        public static LogDetail of(Log log) {
            Location location = log.getLocation();
            LogMetadata logMetadata = new LogMetadata(
                    log.getStartAt(),
                    log.getEndAt(),
                    location.getAddress(),
                    location.getMinLatitude(),
                    location.getMaxLatitude(),
                    location.getMinLongitude(),
                    location.getMaxLongitude()
            );
            List<ImageDTO> imageDTOList = makeImageDTOList(log.getLogImages());
            return new LogDetail(log.getId(), imageDTOList, logMetadata);
        }

        private static List<ImageDTO> makeImageDTOList(List<LogImage> images) {
            List<ImageDTO> imageDTOList = new ArrayList<>();
            for (LogImage image : images) {
                String imageName = image.getName();
                List<ImageEmotionDTO> emotionDTOS = new ArrayList<>();
                Set<Emotion> emotions = image.getEmotions();
                for (Emotion emotion : emotions) {
                    emotionDTOS.add(new ImageEmotionDTO(emotion.getMember().getNickname(), emotion.getEmotion()));
                }
                imageDTOList.add(new ImageDTO(imageName, emotionDTOS));
            }
            return imageDTOList;
        }
    }
}
