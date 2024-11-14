package com.macrosoft.modakserver.domain.image.dto;

import com.macrosoft.modakserver.domain.image.entity.LogImage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ImageResponse {
    public record ImageUrl(
            @Schema(description = "이미지 URL", example = "https://modak-image.s3.ap-northeast-2.amazonaws.com/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String imageUrl) {
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
}
