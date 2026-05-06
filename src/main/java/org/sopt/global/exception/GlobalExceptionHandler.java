package org.sopt.global.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.sopt.global.code.ErrorCode;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleBaseException(BaseException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.warn("Business exception: {}", errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonApiResponse.failureBody(errorCode, exception.getDetails()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST, details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Void> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception
    ) {
        log.warn("Method validation failed: {}", exception.getMessage());
        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("Request body is not readable: {}", exception.getMessage());
        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("Invalid argument: {}", exception.getMessage());
        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Void> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.warn("Database constraint violation: {}", exception.getMessage());
        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler({
            JpaSystemException.class,
            TransactionSystemException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonApiResponse<Void> handlePersistenceException(Exception exception) {
        log.error("Persistence system error occurred", exception);
        return CommonApiResponse.failureBody(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonApiResponse<Void> handleNoResourceFoundException(NoResourceFoundException exception) {
        log.debug("Resource not found: {}", exception.getResourcePath());
        return CommonApiResponse.failureBody(GlobalErrorCode.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonApiResponse<Void> handleException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return CommonApiResponse.failureBody(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }
}
