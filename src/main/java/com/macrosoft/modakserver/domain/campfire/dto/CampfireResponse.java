package com.macrosoft.modakserver.domain.campfire.dto;

import com.macrosoft.modakserver.domain.log.dto.LogResponse.ImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
            OffsetDateTime createdAt,
            Set<String> membersNames) {
        public static CampfireJoinInfo of(String campfireName, LocalDateTime createdAt, Set<String> membersNames) {
            return new CampfireJoinInfo(campfireName, createdAt.atOffset(ZoneOffset.UTC), membersNames);
        }
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
            Set<Long> memberIds,
            @Schema(description = "이미지 이름", example = "/dev/772b94e6-2081-4d1d-b331-20015cc287e0.jpeg")
            String imageName) {
    }
}
