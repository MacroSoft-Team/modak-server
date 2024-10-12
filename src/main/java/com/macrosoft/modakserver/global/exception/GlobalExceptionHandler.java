package com.macrosoft.modakserver.global.exception;

import com.macrosoft.modakserver.global.BaseResponse;
import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import javax.naming.AuthenticationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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
        return createResponseEntity(GlobalErrorCode.INTERNAL_SERVER_ERROR.getErrorCode());
    }

    /**
     * ConstraintViolationException 예외 처리 (Validation 실패)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<String>> handleConstraintViolationException(
            ConstraintViolationException e) {
        return createResponseEntity(GlobalErrorCode.VALIDATION_FAILED.getErrorCode());
    }

    /**
     * MethodArgumentTypeMismatchException 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<String>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {
        return createResponseEntity(GlobalErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH.getErrorCode());
    }

    /**
     * MethodArgumentNotValidException 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<String>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        return createResponseEntity(GlobalErrorCode.METHOD_ARGUMENT_NOT_VALID.getErrorCode());
    }

    /**
     * NoHandlerFoundException 예외 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleNoHandlerFoundException(
            NoHandlerFoundException e) {
        return createResponseEntity(GlobalErrorCode.NO_HANDLER_FOUND.getErrorCode());
    }

    /**
     * DataIntegrityViolationException 예외 처리
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<String>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return createResponseEntity(GlobalErrorCode.DATA_INTEGRITY_VIOLATION.getErrorCode());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<BaseResponse<String>> handleDataAccessException(DataAccessException e) {
        return createResponseEntity(GlobalErrorCode.DATA_ACCESS_ERROR.getErrorCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<String>> handleAuthenticationException(AuthenticationException e) {
        return createResponseEntity(GlobalErrorCode.AUTHENTICATION_FAILED.getErrorCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<String>> handleAccessDeniedException(AccessDeniedException e) {
        return createResponseEntity(GlobalErrorCode.ACCESS_DENIED.getErrorCode());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseResponse<String>> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        return createResponseEntity(GlobalErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getErrorCode());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<BaseResponse<String>> handleHttpClientErrorException(HttpClientErrorException e) {
        return createResponseEntity(GlobalErrorCode.CLIENT_ERROR.getErrorCode());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<BaseResponse<String>> handleHttpServerErrorException(HttpServerErrorException e) {
        return createResponseEntity(GlobalErrorCode.SERVER_ERROR.getErrorCode());
    }

    private ResponseEntity<BaseResponse<String>> createResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.onFailure(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        null));
    }
}