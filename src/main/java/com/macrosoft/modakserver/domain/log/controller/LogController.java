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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public static final String API_CAMPFIRES_LOG_IMAGES = "/{campfirePin}/images";
    private final LogService logService;

    @Operation(summary = "모닥불의 장작들의 메타데이터 정보 가져오기", description = "모닥불에 업로드 되어 있는 장작들의 메타데이터 (위치, 시간) 정보를 가져옵니다. `SelectMergeLogs` 화면에서 추천 장작을 선정할 때 사용됩니다.")
    @GetMapping(API_CAMPFIRES_LOG + "/metadata")
    public BaseResponse<LogMetadataList> getLogsMetadata(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin
    ) {
        return BaseResponse.onSuccess(logService.getLogsMetadata(userDetails.getMember(), campfirePin));
    }

    @Operation(summary = "모닥불에 장작 넣기", description = "모닥불에 장작들을 추가합니다. `SelectMergeLogs` 화면에서 사용됩니다.")
    @PostMapping(API_CAMPFIRES_LOG)
    public BaseResponse<LogResponse.LogId> addLogs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @RequestBody LogRequest.UploadLog log
    ) {
        return BaseResponse.onSuccess(logService.addLogs(userDetails.getMember(), campfirePin, log));
    }

    @Operation(summary = "모닥불의 장작들 미리보기", description = "모닥불에 업로드 되어 있는 장작들의 미리보기 정보를 가져옵니다. 페이지네이션을 지원합니다. `CampfireLogPile` 화면 에서 사용됩니다.")
    @GetMapping(API_CAMPFIRES_LOG)
    public BaseResponse<LogResponse.LogOverviews> getLogOverviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return BaseResponse.onSuccess(logService.getLogOverviews(userDetails.getMember(), campfirePin, page, size));
    }

    @Operation(summary = "장작의 사진들 가져오기", description = "업로드된 장작에 있는 사진들의 아이디와 이름을 가져옵니다. 페이지네이션을 지원합니다 (기본 사이즈: 21). `LogDetail` 화면에서 사용됩니다.")
    @GetMapping(API_CAMPFIRES_LOG + "/{logId}/images")
    public BaseResponse<LogResponse.LogDetails> getLogDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "장작 아이디", example = "1")
            @PathVariable("logId") long logId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "21") int size
    ) {
        return BaseResponse.onSuccess(
                logService.getLogDetails(userDetails.getMember(), campfirePin, logId, page, size));
    }

    @Operation(summary = "모닥불에서 장작 삭제하기", description = "모닥불에 장작을 삭제합니다.")
    @DeleteMapping(API_CAMPFIRES_LOG + "/{logId}")
    public BaseResponse<LogResponse.LogId> removeLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "장작 아이디", example = "1")
            @PathVariable("logId") long logId
    ) {
        return BaseResponse.onSuccess(logService.removeLog(userDetails.getMember(), campfirePin, logId));
    }

    @Operation(summary = "사진 상세보기", description = "모닥불에 업로드된 사진의 세부 정보를 봅니다. 사진의 메타데이터와 감정표현을 불러옵니다. 사진을 눌러서 크게 본 화면에서 사용됩니다.")
    @GetMapping(API_CAMPFIRES_LOG_IMAGES + "/{imageId}")
    public BaseResponse<LogResponse.ImageDetail> getImageDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "사진 아이디", example = "1")
            @PathVariable("imageId") Long imageId
    ) {
        return BaseResponse.onSuccess(logService.getImageDetail(userDetails.getMember(), campfirePin, imageId));
    }

    @Operation(summary = "감정 표현 하기", description = "사진에 대한 감정 표현을 등록합니다.")
    @PutMapping(API_CAMPFIRES_LOG_IMAGES + "/{imageId}/emotions")
    public BaseResponse<LogResponse.ImageDTO> emotion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "사진 아이디", example = "1")
            @PathVariable("imageId") Long imageId,
            @RequestBody LogRequest.EmotionDTO emotionDTO
    ) {
        return BaseResponse.onSuccess(
                logService.emotion(userDetails.getMember(), campfirePin, imageId, emotionDTO.emotion()));
    }

    @Operation(summary = "감정 표현 삭제", description = "사진에 대한 사용자의 감정 표현을 삭제합니다.")
    @DeleteMapping(API_CAMPFIRES_LOG_IMAGES + "/{imageId}/emotions")
    public BaseResponse<LogResponse.ImageDTO> deleteEmotion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "사진 아이디", example = "1")
            @PathVariable("imageId") Long imageId
    ) {
        return BaseResponse.onSuccess(
                logService.deleteEmotion(userDetails.getMember(), campfirePin, imageId)
        );
    }

    @Operation(summary = "장작에서 사진 삭제하기", description = "장작에서 사진들을 삭제합니다.")
    @DeleteMapping(API_CAMPFIRES_LOG + "/{logId}/images")
    public BaseResponse<LogResponse.ImageIds> removeImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "장작 아이디", example = "1")
            @PathVariable("logId") long logId,
            @RequestParam(value = "imageIds") List<Long> imageIds
    ) {
        return BaseResponse.onSuccess(
                logService.removeImages(userDetails.getMember(), campfirePin, logId, imageIds));
    }
}
