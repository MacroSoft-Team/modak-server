package com.macrosoft.modakserver.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCodeInterface {
    INVALID_TOKEN("AUTH001", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("AUTH002", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("AUTH003", "지원되지 않는 토큰입니다.", HttpStatus.UNAUTHORIZED),
    WRONG_TOKEN_SIGNATURE("AUTH004", "토큰의 서명이 잘못됐습니다.", HttpStatus.UNAUTHORIZED),
    EMPTY_TOKEN("AUTH005", "토큰의 Claim 이 비어있습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_VALIDATION_FAIL("AUTH006", "토큰 유효성 검사에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE("AUTH007", "유효하지 않은 토큰 타입입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_CREATE_FAIL("AUTH008", "토큰 생성에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_NOT_HAVE_TOKEN("AUTH009", "회원이 리프레시 토큰 정보를 가지고 있지 않습니다.", HttpStatus.UNAUTHORIZED),
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