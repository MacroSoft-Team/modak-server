package com.macrosoft.modakserver.domain.campfire.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CampfireRequest {
    public record CampfireCreate(
            @Schema(description = "모닥불 이름", defaultValue = "Macrosoft")
            String campfireName) {
    }

    public record CampfireJoin(
            @Schema(description = "참가할 모닥불 이름", defaultValue = "Macrosoft")
            String campfireName) {
    }

    public record CampfireUpdateName(
            @Schema(description = "새로운 모닥불 이름", defaultValue = "Macrosoft")
            String newCampfireName) {
    }
}
