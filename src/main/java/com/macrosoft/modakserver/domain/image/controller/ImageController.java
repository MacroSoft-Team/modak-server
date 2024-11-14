package com.macrosoft.modakserver.domain.image.controller;

import com.macrosoft.modakserver.domain.image.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Image API", description = "장작 이미지 관련 API 입니다.")
public class ImageController {
    private final ImageService imageService;
}
