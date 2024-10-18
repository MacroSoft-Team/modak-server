package com.macrosoft.modakserver.domain.log.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogIds;
import com.macrosoft.modakserver.domain.log.service.LogService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
@Tag(name = "Log API", description = "장작 관련 API 입니다.")
public class LogController {
    private final LogService logService;

    @Operation(summary = "개인 장작 정보 업로드", description = "개인 장작 정보들을 업로드합니다.")
    @PostMapping("/private")
    public BaseResponse<LogIds> uploadPrivateLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody LogRequest.PrivateLogInfos privateLogInfoList
    ) {
        return BaseResponse.onSuccess(logService.uploadPrivateLog(userDetails.getMember(), privateLogInfoList));
    }
}
