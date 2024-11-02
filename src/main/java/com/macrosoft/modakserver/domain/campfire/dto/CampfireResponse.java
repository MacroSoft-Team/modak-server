package com.macrosoft.modakserver.domain.campfire.dto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CampfireResponse {
    public record CampfirePin(
            int campfirePin) {
    }

    public record CampfireInfos(
            List<CampfireInfo> campfireInfos) {
    }

    public record CampfireMain(
            int campfirePin,
            String campfireName,
            Optional<TodayImage> todayImage,
            Set<Long> memberIds) {
    }

    public record CampfireName(
            int campfirePin,
            String campfireName) {
    }

    public record CampfireInfo(
            int campfirePin,
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
