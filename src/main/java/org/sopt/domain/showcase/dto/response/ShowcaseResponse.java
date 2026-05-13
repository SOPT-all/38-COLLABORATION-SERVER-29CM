package org.sopt.domain.showcase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.global.support.pagination.PageInfoResponse;

@Schema(description = "쇼케이스 피드 조회 응답")
public record ShowcaseResponse(
        @Schema(description = "상단 쇼케이스 배너")
        List<ShowcaseItemResponse> featured,

        @Schema(description = "테마별 쇼케이스 섹션 목록")
        List<ShowcaseSectionResponse> sections,

        @Schema(description = "페이지 정보")
        PageInfoResponse pageInfo
) {
}
