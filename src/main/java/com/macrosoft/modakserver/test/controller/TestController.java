package com.macrosoft.modakserver.test.controller;

import com.macrosoft.modakserver.global.BaseResponse;
import com.macrosoft.modakserver.member.model.Member;
import com.macrosoft.modakserver.test.service.TestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final TestService testService;

    @GetMapping("/get")
    public BaseResponse<List<Member>> get() {
        List<Member> memberList = testService.get();
        return BaseResponse.onSuccess(memberList);
    }
}
