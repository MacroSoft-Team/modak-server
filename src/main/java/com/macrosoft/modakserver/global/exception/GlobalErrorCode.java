package com.macrosoft.modakserver.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCodeInterface {
    METHOD_ARGUMENT_TYPE_MISMATCH("AUTH001", "잘못된 인자 타입", HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_NOT_VALID("AUTH002", "인자 유효성 검사 실패", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED("AUTH003", "유효성 검사 실패", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("AUTH004", "서버 내부 에러", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.builder()
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}