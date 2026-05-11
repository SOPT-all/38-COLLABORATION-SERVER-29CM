package org.sopt.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세부 카테고리 응답")
public record SubCategoryResponse(
        @Schema(description = "세부 카테고리 ID", example = "1001")
        Long subCategoryId,

        @Schema(description = "세부 카테고리 이름", example = "단독")
        String name
) {
}
