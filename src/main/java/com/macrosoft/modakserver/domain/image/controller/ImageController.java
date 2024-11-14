package com.macrosoft.modakserver.domain.image.controller;

import static com.macrosoft.modakserver.domain.log.controller.LogController.API_CAMPFIRES_LOG;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.image.dto.ImageRequest;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.image.service.ImageService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Image API", description = "장작 이미지 관련 API 입니다.")
public class ImageController {
    private final ImageService imageService;

    @Operation(summary = "사진 상세보기", description = "모닥불에 업로드된 사진의 세부 정보를 봅니다. 사진의 메타데이터와 감정표현을 불러옵니다. 사진을 눌러서 크게 본 화면에서 사용됩니다.")
    @GetMapping("/campfires/{campfirePin}/images/{imageId}")
    public BaseResponse<ImageResponse.ImageDetail> getImageDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "사진 아이디", example = "1")
            @PathVariable("imageId") Long imageId
    ) {
        return BaseResponse.onSuccess(imageService.getImageDetail(userDetails.getMember(), campfirePin, imageId));
    }

    @Operation(summary = "감정 표현 하기", description = "사진에 대한 감정 표현을 등록합니다.")
    @PutMapping("/campfires/{campfirePin}/images/{imageId}/emotions")
    public BaseResponse<ImageResponse.ImageDTO> emotion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "사진 아이디", example = "1")
            @PathVariable("imageId") Long imageId,
            @RequestBody ImageRequest.EmotionDTO emotionDTO
    ) {
        return BaseResponse.onSuccess(
                imageService.emotion(userDetails.getMember(), campfirePin, imageId, emotionDTO.emotion()));
    }

    @Operation(summary = "감정 표현 삭제", description = "사진에 대한 사용자의 감정 표현을 삭제합니다.")
    @DeleteMapping("/campfires/{campfirePin}/images/{imageId}/emotions")
    public BaseResponse<ImageResponse.ImageDTO> deleteEmotion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "사진 아이디", example = "1")
            @PathVariable("imageId") Long imageId
    ) {
        return BaseResponse.onSuccess(
                imageService.deleteEmotion(userDetails.getMember(), campfirePin, imageId)
        );
    }

    @Operation(summary = "장작에서 사진 삭제하기", description = "장작에서 사진들을 삭제합니다.")
    @DeleteMapping(API_CAMPFIRES_LOG + "/{logId}/images")
    public BaseResponse<ImageResponse.ImageIds> removeImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "모닥불 핀 번호", example = "111111")
            @PathVariable("campfirePin") int campfirePin,
            @Parameter(description = "장작 아이디", example = "1")
            @PathVariable("logId") long logId,
            @RequestParam(value = "imageIds") List<Long> imageIds
    ) {
        return BaseResponse.onSuccess(
                imageService.removeImages(userDetails.getMember(), campfirePin, logId, imageIds));
    }
}
