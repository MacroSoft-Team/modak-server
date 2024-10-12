package com.macrosoft.modakserver.test.controller;

import com.macrosoft.modakserver.global.BaseResponse;
import com.macrosoft.modakserver.global.exception.CustomException;
import com.macrosoft.modakserver.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping
    public ResponseEntity<BaseResponse<String>> home() {
        return ResponseEntity.ok(BaseResponse.onSuccess("HOME"));
    }

    @GetMapping("/test")
    public BaseResponse<String> test() {
        return BaseResponse.onSuccess("TEST");
    }
}
