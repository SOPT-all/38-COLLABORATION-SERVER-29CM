package org.sopt.domain.showcase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "쇼케이스 섹션")
public record ShowcaseSectionResponse(
        @Schema(description = "섹션 ID", example = "1")
        Long sectionId,

        @Schema(description = "섹션 테마", example = "LIFESTYLE")
        String theme,

        @Schema(description = "섹션 제목", example = "당신의 취향에 맞춘 잡화 셀렉션")
        String title,

        @Schema(description = "섹션 하위 쇼케이스 목록")
        List<ShowcaseItemResponse> showcases
) {
}
