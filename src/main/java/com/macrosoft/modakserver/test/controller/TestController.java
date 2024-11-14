package com.macrosoft.modakserver.test.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.global.BaseResponse;
import com.macrosoft.modakserver.test.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Tag(name = "Test API")
public class TestController {
    private final TestService testService;

    @Operation(summary = "테스트용 임시 데이터 생성하기", description = "현재 로그인한 사용자가 모닥불을 생성하고 테스트용 유저 4명을 들어오게 합니다. 장작과 이미지와 감정표현이 모두 무작위로 생성됩니다.")
    @PostMapping("add-mock-data")
    public BaseResponse<CampfireResponse.CampfireMain> addMockData(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return BaseResponse.onSuccess(testService.addMockData(userDetails.getMember()));
    }
}
