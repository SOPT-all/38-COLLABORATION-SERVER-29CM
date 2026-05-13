package org.sopt.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import org.sopt.global.support.pagination.PageInfoResponse;

@Schema(description = "홈 메인 조회 응답")
public record HomeMainResponse(
        @Schema(description = "카테고리 숏컷 목록")
        List<HomeShortcutResponse> shortcuts,

        @Schema(description = "홈 큐레이션 섹션 목록")
        List<HomeSectionResponse> sections,

        @Schema(description = "커서 기반 페이지 정보")
        PageInfoResponse pageInfo
) {
}
