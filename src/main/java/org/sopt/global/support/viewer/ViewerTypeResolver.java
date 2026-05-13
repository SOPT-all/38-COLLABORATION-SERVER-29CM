package org.sopt.global.support.viewer;

import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BaseException;
import org.springframework.stereotype.Component;

@Component
public class ViewerTypeResolver {

    private static final String GUEST = "guest";
    private static final String USER = "user";

    public ViewerType resolve(String rawViewerType) {
        if (rawViewerType == null) {
            return ViewerType.GUEST;
        }

        return switch (rawViewerType) {
            case GUEST -> ViewerType.GUEST;
            case USER -> ViewerType.USER;
            default -> throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        };
    }
}
