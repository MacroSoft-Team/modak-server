package com.macrosoft.modakserver.domain.campfire.dto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CampfireResponse {
    public record CampfireId(
            Long campfireId) {
    }

    public record CampfireInfos(
            List<CampfireInfo> campfireInfos) {
    }

    public record CampfireMain(
            Long campfireId,
            String campfireName,
            Optional<TodayImage> todayImage,
            Set<Long> memberIds) {
    }

    public record CampfireName(
            Long campfireId,
            String campfireName) {
    }

    public record CampfireInfo(
            Long campfireId,
            String campfireName,
            Set<String> membersNames,
            String imageName) {
    }

    private record TodayImage(
            String imageName,
            List<ImageEmote> imageEmotes) {
    }

    private record ImageEmote(
            String memberName,
            String emote) {
    }
}
