package com.macrosoft.modakserver.domain.log.dto;

import com.macrosoft.modakserver.domain.log.entity.LogImage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public record ImageDTO(
            @Schema(description = "이미지 ID", example = "1")
            Long imageId,
            @Schema(description = "이미지 이름", example = "/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String name,
            Set<ImageEmotionDTO> emotions) {

        public static ImageDTO of(LogImage logImage) {
            return new ImageDTO(
                    logImage.getId(),
                    logImage.getName(),
                    logImage.getEmotions().stream()
                            .map(emotion -> new ImageEmotionDTO(
                                    emotion.getMember().getNickname(),
                                    emotion.getEmotion()
                            ))
                            .collect(Collectors.toUnmodifiableSet()));
        }
    }

    public record ImageDetail(
            @Schema(description = "이미지 ID", example = "1")
            Long imageId,
            @Schema(description = "이미지 이름", example = "/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String imageName,
            double latitude,
            double longitude,
            LocalDateTime takenAt,
            String memberNickname,
            Long logId,
            List<ImageEmotionDTO> emotions) {
        public static ImageDetail of(LogImage logImage) {
            return new ImageDetail(
                    logImage.getId(),
                    logImage.getName(),
                    logImage.getLatitude(),
                    logImage.getLongitude(),
                    logImage.getTakenAt(),
                    logImage.getMember().getNickname(),
                    logImage.getLog().getId(),
                    logImage.getEmotions().stream()
                            .map(emotion -> new ImageEmotionDTO(
                                    emotion.getMember().getNickname(),
                                    emotion.getEmotion()
                            ))
                            .toList()
            );
        }
    }

    public record ImageName(
            @Schema(description = "이미지 ID", example = "1")
            Long imageId,
            @Schema(description = "이미지 이름", example = "/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String imageName) {
    }

    public record ImageEmotionDTO(
            @Schema(description = "멤버 이름", example = "음침한 윤겸핑")
            String memberNickname,
            @Schema(description = "감정표현", example = "😀")
            String emotion) {
    }

    public record ImageIds(
            List<Long> imageIds) {
    }
}
