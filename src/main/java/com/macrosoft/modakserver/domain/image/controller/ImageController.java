package com.macrosoft.modakserver.domain.image.controller;

import com.macrosoft.modakserver.domain.image.dto.ImageResponse;
import com.macrosoft.modakserver.domain.image.service.ImageService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "Image API", description = "S3로 이미지를 업로드 하는 API 입니다.")
public class ImageController {
    private final ImageService imageService;

    @Operation(summary = "사진 업로드", description = "S3 버켓에 사진을 업로드하여 해당 사진의 URL 을 반환합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ImageResponse.ImageUrl> uploadAvatarImage(@RequestPart(value = "image") MultipartFile image) {
        return BaseResponse.onSuccess(imageService.uploadImage(image));
    }
}
