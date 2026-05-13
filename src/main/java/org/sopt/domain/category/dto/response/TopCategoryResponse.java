package org.sopt.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "대분류 카테고리 응답")
public record TopCategoryResponse(
        @Schema(description = "대분류 ID", example = "1")
        Long topCategoryId,

        @Schema(description = "대분류 이름", example = "BEST")
        String name,

        @Schema(description = "대분류 하위 중분류 목록")
        List<MiddleCategoryResponse> middleCategories
) {
}
