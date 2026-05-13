package org.sopt.global.support.cursor;

import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BaseException;

public interface CursorPayload {

    void validate();

    static void validateNonNegative(Integer value) {
        if (value == null || value < 0) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
    }

    static void validateNonNegative(Long value) {
        if (value == null || value < 0) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
    }
}
