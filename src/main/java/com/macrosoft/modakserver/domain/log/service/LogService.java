package com.macrosoft.modakserver.domain.log.service;

import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;

public interface LogService {
    LogResponse.logIdList uploadPrivateLog(Member member, LogRequest.PrivateLogInfoList privateLogInfoList);
}
