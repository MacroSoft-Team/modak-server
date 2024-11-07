package com.macrosoft.modakserver.domain.log.dto;

import java.util.List;

public class LogRequest {
    public record UploadLogs(List<UploadLog> logs) {
    }

    public record UploadLog(
            LogResponse.LogMetadata logMetadata,
            List<String> imageNames) {
    }
}
