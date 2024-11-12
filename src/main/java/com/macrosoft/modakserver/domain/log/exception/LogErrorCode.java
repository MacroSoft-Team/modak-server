package com.macrosoft.modakserver.domain.log.exception;

import com.macrosoft.modakserver.global.exception.ErrorCode;
import com.macrosoft.modakserver.global.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LogErrorCode implements ErrorCodeInterface {
    LOG_NOT_FOUND("L001", "존재하지 않는 장작입니다.", HttpStatus.NOT_FOUND),
    LOG_CAMPFIRE_NOT_MATCH("L002", "장작과 캠프파이어가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    LOGS_EMPTY("L003", "장작 계산중에 장작이 없는 오류가 발생했습니다.", HttpStatus.NOT_FOUND),
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