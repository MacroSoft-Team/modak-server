package com.macrosoft.modakserver.domain.log.service;

import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogMetadataList;
import com.macrosoft.modakserver.domain.log.entity.Log;
import com.macrosoft.modakserver.domain.log.entity.LogImage;
import com.macrosoft.modakserver.domain.member.entity.Member;
import java.util.List;

public interface LogService {
    LogMetadataList getLogsMetadata(Member member, int campfirePin);

    LogResponse.LogId addLogs(Member member, int campfirePin, LogRequest.UploadLog uploadLog);

    LogResponse.LogOverviews getLogOverviews(Member member, int campfirePin, int page, int size);

    LogResponse.LogDetails getLogDetails(Member member, int campfirePin, Long logId, int page, int size);

    LogResponse.LogId removeLog(Member member, int campfirePin, Long logId);

    LogResponse.ImageDetail getImageDetail(Member member, int campfirePin, Long imageId);

    LogResponse.ImageDTO emotion(Member member, int campfirePin, Long imageId, String emotion);

    LogResponse.ImageDTO deleteEmotion(Member member, int campfirePin, Long imageId);

    LogResponse.ImageIds removeImages(Member member, int campfirePin, Long logId, List<Long> imageIds);

    LogImage getLogImage(Long imageId);

    Log getLog(Long logId);
}
