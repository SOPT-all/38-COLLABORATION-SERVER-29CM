package org.sopt.domain.showcase.cursor;

import org.sopt.global.support.cursor.CursorPayload;

public record ShowcaseCursorPayload(
        Integer sectionDisplayOrder,
        Long sectionId,
        Integer showcaseDisplayOrder,
        Long showcaseId
) implements CursorPayload {

    @Override
    public void validate() {
        CursorPayload.validateNonNegative(sectionDisplayOrder);
        CursorPayload.validateNonNegative(sectionId);
        CursorPayload.validateNonNegative(showcaseDisplayOrder);
        CursorPayload.validateNonNegative(showcaseId);
    }
}
