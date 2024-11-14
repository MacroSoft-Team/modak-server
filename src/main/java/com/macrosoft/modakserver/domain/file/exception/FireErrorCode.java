package com.macrosoft.modakserver.domain.file.exception;

import com.macrosoft.modakserver.global.exception.ErrorCode;
import com.macrosoft.modakserver.global.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FireErrorCode implements ErrorCodeInterface {
    INVALID_FILE_EXTENSION("F001", "파일 확장자가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
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