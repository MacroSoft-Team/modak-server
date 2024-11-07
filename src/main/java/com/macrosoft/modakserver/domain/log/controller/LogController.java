package com.macrosoft.modakserver.domain.log.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.log.dto.LogRequest;
import com.macrosoft.modakserver.domain.log.dto.LogResponse;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogMetadataList;
import com.macrosoft.modakserver.domain.log.service.LogService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campfires")
@Tag(name = "Log API", description = "모닥불의 장작 관련 API 입니다.")
public class LogController {
    public static final String API_CAMPFIRES_LOG = "/{campfirePin}/logs";
    private final LogService logService;

    @Operation(summary = "모닥불의 장작들의 메타데이터 정보 가져오기", description = "모닥불에 업로드 되어 있는 장작들의 메타데이터 (위치, 시간) 정보를 가져옵니다. 클라이언트에서 추천 장작을 선정할 때 사용됩니다.")
    @GetMapping(API_CAMPFIRES_LOG + "/metadata")
    public BaseResponse<LogMetadataList> getLogsMetadata(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin
    ) {
        return BaseResponse.onSuccess(logService.getLogsMetadata(userDetails.getMember(), campfirePin));
    }

    @Operation(summary = "모닥불에 장작 넣기", description = "모닥불에 장작들을 추가합니다.")
    @PostMapping(API_CAMPFIRES_LOG)
    public BaseResponse<LogResponse.LogDTO> addLogs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @RequestBody LogRequest.UploadLog log
    ) {
        return BaseResponse.onSuccess(logService.addLogs(userDetails.getMember(), campfirePin, log));
    }

    @Operation(summary = "모닥불의 장작들 가져오기", description = "모닥불에 업로드 되어 있는 장작들의 정보를 모두 가져옵니다. 페이지네이션을 지원합니다.")
    @GetMapping(API_CAMPFIRES_LOG)
    public BaseResponse<LogResponse.Logs> getLogs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return BaseResponse.onSuccess(logService.getLogs(userDetails.getMember(), campfirePin, page, size));
    }

//    @Operation(summary = "모닥불에 장작 빼기", description = "모닥불에 장작들을 제거합니다.")
//    @DeleteMapping(API_CAMPFIRES_LOG)
//    public BaseResponse<LogResponse.LogIds> removeLogs(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @Parameter(description = "모닥불 핀 번호", example = "000000")
//            @PathVariable("campfirePin") int campfirePin
//            @RequestBody CampfireRequest.Logs logs
//    ) {
//        return BaseResponse.onSuccess(logService.removeLogs(userDetails.getMember(), campfirePin, logs));
//        return null;
//    }
}
