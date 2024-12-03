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
    EMPTY_FILE_EXCEPTION("I001", "업로드된 파일이 없거나 파일 이름이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    IO_EXCEPTION_ON_IMAGE_UPLOAD("I002", "이미지 업로드 중에 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_FILE_EXTENTION("I003", "파일에 확장자가 포함되어 있지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_EXTENTION("I004", "지원되지 않는 파일 형식입니다. (허용 형식: jpg, jpeg, png, gif, webp, heif)",
            HttpStatus.BAD_REQUEST),
    PUT_OBJECT_EXCEPTION("I005", "이미지를 S3에 저장하는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IO_EXCEPTION_ON_IMAGE_DELETE("I006", "S3에서 이미지를 삭제하는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    MALFORMED_URL_EXCEPTION("I007", "이미지 주소 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_ENCODING_EXCEPTION("I008", "이미지 주소의 인코딩 방식이 지원되지 않습니다.", HttpStatus.BAD_REQUEST),
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