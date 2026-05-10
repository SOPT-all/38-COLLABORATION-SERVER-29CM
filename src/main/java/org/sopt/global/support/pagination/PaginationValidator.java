package org.sopt.global.support.pagination;

import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BaseException;

public final class PaginationValidator {

    private PaginationValidator() {
    }

    public static int resolveSize(Integer rawSize, int defaultSize, int minSize, int maxSize) {
        int size = rawSize == null ? defaultSize : rawSize;
        if (size < minSize || size > maxSize) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
        return size;
    }
}
