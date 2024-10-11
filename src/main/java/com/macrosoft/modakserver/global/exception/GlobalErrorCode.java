package com.macrosoft.modakserver.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCodeInterface {
    METHOD_ARGUMENT_TYPE_MISMATCH("GLOBAL001", "잘못된 인자 타입", HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_NOT_VALID("GLOBAL002", "인자 유효성 검사 실패", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED("GLOBAL003", "유효성 검사 실패", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("GLOBAL004", "서버 내부 에러", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_HANDLER_FOUND("GLOBAL005", "잘못된 URI 호출", HttpStatus.NOT_FOUND),
    DATA_INTEGRITY_VIOLATION("GLOBAL006", "데이터 무결성 위반", HttpStatus.BAD_REQUEST),
    DATA_ACCESS_ERROR("GLOBAL007", "데이터베이스 접근 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    AUTHENTICATION_FAILED("GLOBAL008", "인증 실패", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("GLOBAL009", "접근 거부", HttpStatus.FORBIDDEN),
    METHOD_NOT_SUPPORTED("GLOBAL010", "지원하지 않는 HTTP 메서드", HttpStatus.METHOD_NOT_ALLOWED),
    MEDIA_TYPE_NOT_SUPPORTED("GLOBAL011", "지원하지 않는 미디어 타입", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    CLIENT_ERROR("GLOBAL012", "클라이언트 오류", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("GLOBAL013", "서버 오류", HttpStatus.INTERNAL_SERVER_ERROR)
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