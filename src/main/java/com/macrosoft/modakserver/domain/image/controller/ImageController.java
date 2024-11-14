package com.macrosoft.modakserver.domain.image.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.image.service.ImageService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Image API", description = "장작 이미지 관련 API 입니다.")
public class ImageController {
    private final ImageService imageService;

//    @Operation(summary = "사진 업로드", deprecated = true, description = "S3 버켓에 사진을 업로드하여 해당 사진의 URL 을 반환합니다.")
//    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public BaseResponse<ImageResponse.ImageUrl> uploadAvatarImage(@RequestPart(value = "image") MultipartFile image) {
//        return BaseResponse.onSuccess(imageService.uploadImage(image));
//    }

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
}
