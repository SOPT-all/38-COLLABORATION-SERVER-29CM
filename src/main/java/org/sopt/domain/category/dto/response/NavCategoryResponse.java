package org.sopt.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카테고리 메가 메뉴 응답")
public record NavCategoryResponse(
        @Schema(description = "대분류 카테고리 목록")
        List<TopCategoryResponse> categories
) {
}
