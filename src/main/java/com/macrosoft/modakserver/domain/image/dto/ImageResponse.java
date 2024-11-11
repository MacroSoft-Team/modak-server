package com.macrosoft.modakserver.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ImageResponse {
    public record ImageUrl(
            @Schema(description = "이미지 URL", example = "https://modak-image.s3.ap-northeast-2.amazonaws.com/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String imageUrl) {
    }

    public record ImageDTO(
            @Schema(description = "이미지 이름", example = "/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String name,
            List<ImageEmotionDTO> emotions) {
    }

    public record ImageName(
            @Schema(description = "이미지 이름", example = "/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String imageName) {
    }

    public record ImageEmotionDTO(
            @Schema(description = "멤버 이름", example = "음침한 윤겸핑")
            String memberNickname,
            @Schema(description = "감정표현", example = "😀")
            String emotion) {
    }

    public record ImageId(
            @Schema(description = "이미지 ID", example = "1")
            Long imageId) {
    }
}
