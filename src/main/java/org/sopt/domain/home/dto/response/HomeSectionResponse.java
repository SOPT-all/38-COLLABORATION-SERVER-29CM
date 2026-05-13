package org.sopt.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "홈 큐레이션 섹션 응답")
public record HomeSectionResponse(
        @Schema(description = "홈 섹션 ID", example = "1")
        Long sectionId,

        @Schema(description = "홈 섹션 제목")
        String title,

        @Schema(description = "홈 섹션 설명")
        String description,

        @Schema(description = "홈 섹션 대표 이미지 URL")
        String heroImageUrl,

        @Schema(description = "섹션 우측에 노출되는 셀렉션 카드 목록")
        List<HomeSelectionResponse> selections
) {
}
