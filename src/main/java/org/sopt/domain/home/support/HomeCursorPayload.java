package org.sopt.domain.home.support;

import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BaseException;
import org.sopt.global.support.cursor.CursorPayload;
import org.sopt.global.support.viewer.ViewerType;

public record HomeCursorPayload(
        String viewerType,
        Long likedProductCount,
        Integer displayOrder,
        Long sectionId
) implements CursorPayload {

    private static final String GUEST = "guest";
    private static final String USER = "user";

    public static HomeCursorPayload guest(Integer displayOrder, Long sectionId) {
        return new HomeCursorPayload(GUEST, null, displayOrder, sectionId);
    }

    public static HomeCursorPayload user(Long likedProductCount, Integer displayOrder, Long sectionId) {
        return new HomeCursorPayload(USER, likedProductCount, displayOrder, sectionId);
    }

    @Override
    public void validate() {
        validateViewerType();
        CursorPayload.validateNonNegative(displayOrder);
        CursorPayload.validateNonNegative(sectionId);

        if (USER.equals(viewerType)) {
            CursorPayload.validateNonNegative(likedProductCount);
            return;
        }

        if (likedProductCount != null) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
    }

    public void validateViewerType(ViewerType requestedViewerType) {
        if (!viewerType.equals(toRawViewerType(requestedViewerType))) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
    }

    private void validateViewerType() {
        if (!GUEST.equals(viewerType) && !USER.equals(viewerType)) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
    }

    private String toRawViewerType(ViewerType requestedViewerType) {
        return switch (requestedViewerType) {
            case GUEST -> GUEST;
            case USER -> USER;
        };
    }
}
