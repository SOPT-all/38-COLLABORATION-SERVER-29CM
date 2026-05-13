package org.sopt.domain.showcase.cursor;

import org.sopt.global.support.cursor.CursorPayload;

public record ShowcaseCursorPayload(
        Integer lastDisplayOrder,
        Long lastId
) implements CursorPayload {

    @Override
    public void validate() {
        CursorPayload.validateNonNegative(lastDisplayOrder);
        CursorPayload.validateNonNegative(lastId);
    }
}
