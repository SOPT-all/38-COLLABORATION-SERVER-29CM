package org.sopt.global.support.pagination;

import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BaseException;

public final class PaginationValidator {

    private PaginationValidator() {
    }

    public static int resolveAndValidateSize(Integer rawSize, int defaultSize, int minSize, int maxSize) {
        validateSize(defaultSize, minSize, maxSize);

        int size = rawSize == null ? defaultSize : rawSize;
        validateSize(size, minSize, maxSize);

        return size;
    }

    private static void validateSize(int size, int minSize, int maxSize) {
        if (size < minSize || size > maxSize) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
    }
}
