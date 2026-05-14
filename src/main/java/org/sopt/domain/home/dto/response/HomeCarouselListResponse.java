package org.sopt.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "홈 캐러셀 목록 응답")
public record HomeCarouselListResponse(
        @Schema(description = "홈 상단 캐러셀 이미지 목록")
        List<HomeCarouselResponse> carousels
) {
}
