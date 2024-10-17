package com.macrosoft.modakserver.global;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"timeStamp", "code", "message", "result"})
public class BaseResponse<T> {
    private final LocalDateTime timeStamp = LocalDateTime.now();
    private final String code;
    private final String message;
    private T result;

    public static <T> BaseResponse<T> onSuccess(T result) {
        return new BaseResponse<>("COMMON200", "요청이 성공적으로 처리되었습니다.", result);
    }

    public static <T> BaseResponse<T> onFailure(String code, String message, T result) {
        return new BaseResponse<>(code, message, result);
    }
}