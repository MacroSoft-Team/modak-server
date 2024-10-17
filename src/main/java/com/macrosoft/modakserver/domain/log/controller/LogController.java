package com.macrosoft.modakserver.domain.log.controller;

import com.macrosoft.modakserver.domain.log.service.LogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
@Tag(name = "Log API", description = "장작 관련 API 입니다.")
public class LogController {
    private final LogService logService;
}
