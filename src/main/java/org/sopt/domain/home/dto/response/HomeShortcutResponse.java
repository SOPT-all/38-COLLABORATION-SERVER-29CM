package org.sopt.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 카테고리 숏컷 응답")
public record HomeShortcutResponse(
        @Schema(description = "숏컷 ID", example = "1")
        Long shortcutId,

        @Schema(description = "숏컷 이름", example = "BEST")
        String name,

        @Schema(description = "숏컷 이미지 URL")
        String imageUrl,

        @Schema(description = "연결된 카테고리 ID", example = "1")
        Long categoryId
) {
}
