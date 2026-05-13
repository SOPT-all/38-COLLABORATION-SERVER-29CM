package org.sopt.domain.home.support;

import org.sopt.global.support.cursor.CursorPayload;

public record HomeCursorPayload(
        Integer displayOrder,
        Long sectionId
) implements CursorPayload {

    @Override
    public void validate() {
        CursorPayload.validateNonNegative(displayOrder);
        CursorPayload.validateNonNegative(sectionId);
    }
}
