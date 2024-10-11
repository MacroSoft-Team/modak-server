package com.macrosoft.modakserver.global.exception;

import com.macrosoft.modakserver.global.BaseResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 을 처리하는 핸들러
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<String>> handleCustomException(CustomException e) {
        return createResponseEntity(e.getErrorCode());
    }

    /**
     * 일반적인 예외를 처리하는 핸들러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> handleGeneralException(Exception e) {
        return createResponseEntity(
                GlobalErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
                e.getMessage());
    }

    /**
     * ConstraintViolationException 예외 처리 (Validation 실패)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<String>> handleConstraintViolationException(
            ConstraintViolationException e) {
        return createResponseEntity(
                GlobalErrorCode.VALIDATION_FAILED.getErrorCode(),
                e.getConstraintViolations() + "");
    }

    /**
     * MethodArgumentTypeMismatchException 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<String>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {
        return createResponseEntity(
                GlobalErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH.getErrorCode(),
                e.getName() + ": " + e.getValue());
    }

    /**
     * MethodArgumentNotValidException 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<String>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        return createResponseEntity(
                GlobalErrorCode.METHOD_ARGUMENT_NOT_VALID.getErrorCode(),
                e.getBindingResult().getAllErrors() + "");
    }

    private ResponseEntity<BaseResponse<String>> createResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.onFailure(errorCode.getCode(),errorCode.getMessage(), null));
    }

    private ResponseEntity<BaseResponse<String>> createResponseEntity(ErrorCode errorCode, String errorInfo) {
        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.onFailure(errorCode.getCode(),errorCode.getMessage(), errorInfo));
    }
}