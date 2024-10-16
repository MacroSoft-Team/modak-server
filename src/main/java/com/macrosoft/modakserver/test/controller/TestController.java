package com.macrosoft.modakserver.test.controller;

import com.macrosoft.modakserver.global.BaseResponse;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.test.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Tag(name = "Test API")
public class TestController {
    private final TestService testService;

    @GetMapping("/get")
    public BaseResponse<List<Member>> get() {
        List<Member> memberList = testService.get();
        return BaseResponse.onSuccess(memberList);
    }
}
