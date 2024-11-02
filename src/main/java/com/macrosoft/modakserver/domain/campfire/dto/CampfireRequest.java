package com.macrosoft.modakserver.domain.campfire.dto;

public class CampfireRequest {
    public record CampfireCreate(String campfireName) {
    }

    public record CampfireJoin(String campfireName) {
    }

    public record CampfireUpdateName(String newCampfireName) {
    }
}
