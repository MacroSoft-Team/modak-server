package com.macrosoft.modakserver.global.exception;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCodeInterface errorCode;

    public ErrorCode getErrorCode() {
        return this.errorCode.getErrorCode();
    }
}
