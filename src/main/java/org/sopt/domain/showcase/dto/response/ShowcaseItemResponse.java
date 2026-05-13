package org.sopt.domain.showcase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "쇼케이스 항목")
public record ShowcaseItemResponse(
        @Schema(description = "쇼케이스 ID", example = "1")
        Long showcaseId,

        @Schema(description = "쇼케이스 제목", example = "2026 S/S 트렌드")
        String title,

        @Schema(description = "쇼케이스 설명", example = "봄 여름 시즌 트렌드 아이템 모음")
        String description,

        @Schema(description = "쇼케이스 이미지 URL", example = "https://example.com/showcase/1.png")
        String imageUrl,

        @Schema(description = "쇼케이스 시작일", example = "2026-03-01")
        LocalDate startDate,

        @Schema(description = "쇼케이스 종료일", example = "2026-05-14")
        LocalDate endDate
) {
}
