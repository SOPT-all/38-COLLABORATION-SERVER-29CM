package org.sopt.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "중분류 카테고리 응답")
public record MiddleCategoryResponse(
        @Schema(description = "중분류 ID", example = "101")
        Long middleCategoryId,

        @Schema(description = "중분류 이름", example = "의류")
        String name,

        @Schema(description = "중분류 하위 세부 카테고리 목록")
        List<SubCategoryResponse> subCategories
) {
}
