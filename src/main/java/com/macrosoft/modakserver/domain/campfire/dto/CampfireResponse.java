package com.macrosoft.modakserver.domain.campfire.dto;

import com.macrosoft.modakserver.domain.image.dto.ImageResponse.ImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class CampfireResponse {
    public record CampfirePin(
            @Schema(description = "모닥불 핀 번호", example = "111111")
            int campfirePin) {
    }

    public record CampfireJoinInfo(
            @Schema(description = "모닥불 이름", example = "매크로")
            String campfireName,
            @Schema(description = "모닥불 개설시간", example = "2024-11-06T02:21:05.43212")
            LocalDateTime createdAt,
            Set<String> membersNames) {
    }

    public record CampfireInfos(
            List<CampfireInfo> campfireInfos) {
    }

    public record CampfireMain(
            @Schema(description = "모닥불 핀 번호", example = "111111")
            int campfirePin,
            @Schema(description = "모닥불 이름", example = "매크로")
            String campfireName,
            ImageDTO todayImage,
            Set<Long> memberIds) {
    }

    public record CampfireName(
            @Schema(description = "모닥불 핀 번호", example = "111111")
            int campfirePin,
            @Schema(description = "모닥불 이름", example = "매크로")
            String campfireName) {
    }

    public record CampfireInfo(
            @Schema(description = "모닥불 핀 번호", example = "111111")
            int campfirePin,
            @Schema(description = "모닥불 이름", example = "매크로")
            String campfireName,
            Set<String> membersNames,
            @Schema(description = "이미지 이름", example = "/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String imageName) {
    }
}
