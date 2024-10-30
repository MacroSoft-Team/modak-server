package com.macrosoft.modakserver.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ImageResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageUrl {
        @Schema(description = "이미지 URL", example = "https://example.com/image.png")
        private String imageUrl;
    }
}
