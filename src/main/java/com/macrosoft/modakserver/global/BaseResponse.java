package com.macrosoft.modakserver.global;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"timeStamp", "code", "message", "data"})
public class BaseResponse<T> {
    private final LocalDateTime timeStamp = LocalDateTime.now();
    private final String code;
    private final String message;
    private T data;

    public static <T> BaseResponse<T> onSuccess(T data) {
        return new BaseResponse<>("COMMON200", "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> BaseResponse<T> onFailure(String code, String message, T data) {
        return new BaseResponse<>(code, message, data);
    }
}