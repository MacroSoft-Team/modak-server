package com.macrosoft.modakserver.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ImageRequest {
    public record EmotionDTO(
            @Schema(description = "감정표현", example = "😀")
            String emotion) {
    }
}
