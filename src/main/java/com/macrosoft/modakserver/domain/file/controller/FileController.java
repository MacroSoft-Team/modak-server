package com.macrosoft.modakserver.domain.file.controller;

import com.macrosoft.modakserver.domain.file.service.FileService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@Tag(name = "File API", description = "S3로 파일를 업로드 하기 위해 사용되는 API 입니다.")
public class FileController {
    private final FileService fileService;

    @Operation(summary = "미리 서명된 URL 받기", description = "S3 버켓에 파일을 업로드하기 위한 미리 서명된 URL을 받습니다.")
    @GetMapping("/presigned-url/{extension}")
    public BaseResponse<Map<String, String>> getPresignedUrl(
            @Parameter(description = "파일 확장자", example = "webp")
            @PathVariable(name = "extension")
            String extension) {
        return BaseResponse.onSuccess(fileService.getPresignedUrl(extension));
    }
}
