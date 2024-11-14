package com.macrosoft.modakserver.domain.log.exception;

import static com.macrosoft.modakserver.domain.log.service.LogServiceImpl.MAX_EMOTE_LENGTH;

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
    LOG_IMAGE_NOT_FOUND("L004", "해당 이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LOG_IMAGE_NOT_IN_CAMPFIRE("L005", "해당 이미지는 해당 모닥불에 속해 있지 않습니다.", HttpStatus.BAD_REQUEST),
    EMOTE_EMPTY("L006", "감정 표현이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    EMOTE_TOO_LONG("L007", "감정 표현은 " + MAX_EMOTE_LENGTH + "자 이하로 입력해주세요. 이모지는 2자로 취급됩니다.", HttpStatus.BAD_REQUEST),
    EMOTION_NOT_FOUND("L008", "해당 이미지에 사용자의 감정 표현을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EMOTION_NOT_UPLOAD_USER("L009", "해당 감정 표현은 업로드한 사용자가 아닙니다.", HttpStatus.BAD_REQUEST),
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