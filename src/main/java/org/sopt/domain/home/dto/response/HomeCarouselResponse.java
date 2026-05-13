package org.sopt.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 캐러셀 항목 응답")
public record HomeCarouselResponse(
        @Schema(description = "캐러셀 ID", example = "1")
        Long carouselId,

        @Schema(description = "캐러셀 이미지 URL")
        String imageUrl,

        @Schema(description = "이미지 대체 텍스트", example = "여름 시즌 기획전 배너")
        String altText
) {
}
