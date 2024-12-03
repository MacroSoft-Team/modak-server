package com.macrosoft.modakserver.domain.member.exception;

import com.macrosoft.modakserver.global.exception.ErrorCode;
import com.macrosoft.modakserver.global.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCodeInterface {
    MEMBER_ALREADY_EXIST("M001", "이미 존재하는 회원입니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND("M002", "존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND),
    MEMBER_NICKNAME_EMPTY("M003", "닉네임이 비어있습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NICKNAME_TOO_SHORT("M004", "닉네임이 너무 짧습니다. 2자 이상 15자 이하로 가능합니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NICKNAME_TOO_LONG("M005", "닉네임이 너무 깁니다. 2자 이상 15자 이하로 가능합니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NICKNAME_SAME("M006", "기존 닉네임과 동일합니다.", HttpStatus.BAD_REQUEST),
    MEMBER_AVATAR_TYPE_INVALID("M007", "아바타 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
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