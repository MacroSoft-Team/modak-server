package com.macrosoft.modakserver.domain.log.service;

import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogMetadataList;
import com.macrosoft.modakserver.domain.member.entity.Member;

public interface LogService {
    LogMetadataList getLogsMetadata(Member member, int campfirePin);

    LogResponse.LogDTO addLogs(Member member, int campfirePin, LogRequest.UploadLog uploadLog);

    LogResponse.Logs getLogs(Member member, int campfirePin, int page, int size);
}
