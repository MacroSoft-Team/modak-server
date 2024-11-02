package com.macrosoft.modakserver.domain.campfire.exception;

import com.macrosoft.modakserver.global.exception.ErrorCode;
import com.macrosoft.modakserver.global.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CampfireErrorCode implements ErrorCodeInterface {
    CAMPFIRE_NOT_FOUND_BY_PIN("C001", "핀 번호가 일치하는 모닥불이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    CAMPFIRE_NAME_EMPTY("C002", "모닥불 이름이 비어있습니다.", HttpStatus.BAD_REQUEST),
    CAMPFIRE_NAME_TOO_LONG("C003", "모닥불 이름이 너무 깁니다. 12자 이하로 가능합니디.", HttpStatus.BAD_REQUEST),
    CAMPFIRE_NAME_NOT_MATCH("C004", "모닥불 이름이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_IN_CAMPFIRE("C005", "모닥불에 참여하지 않은 멤버입니다.", HttpStatus.BAD_REQUEST),
    REMOVE_FROM_CAMPFIRE_MEMBER_NOT_IN_CAMPFIRE("C006", "모닥불에 참여하지 않은 멤버를 모닥불에서 제거할 수 없습니다.", HttpStatus.BAD_REQUEST),
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