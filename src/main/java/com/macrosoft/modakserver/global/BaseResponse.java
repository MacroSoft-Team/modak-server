package com.macrosoft.modakserver.global;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "data"})
public class BaseResponse<T> {
    private String code;
    private String message;
    private T data;

    public static <T> BaseResponse<T> onSuccess(T data) {
        return BaseResponse.<T>builder()
                .code("200")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> onFailure(String code, String message, T data) {
        return BaseResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}