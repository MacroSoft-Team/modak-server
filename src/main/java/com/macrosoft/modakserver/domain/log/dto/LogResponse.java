package com.macrosoft.modakserver.domain.log.dto;

import java.util.List;

public class LogResponse {
    public record LogIds(List<Long> logIds) {
    }
}
