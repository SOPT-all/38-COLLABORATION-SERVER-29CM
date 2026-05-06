package org.sopt.global.exception;

import java.util.Map;
import lombok.Getter;
import org.sopt.global.code.ErrorCode;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public BaseException(ErrorCode errorCode) {
        this(errorCode, Map.of());
    }

    public BaseException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }
}
