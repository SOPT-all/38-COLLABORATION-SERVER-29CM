package org.sopt.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "홈 셀렉션 카드 응답")
public record HomeSelectionResponse(
        @Schema(description = "셀렉션 ID", example = "11")
        Long selectionId,

        @Schema(description = "셀렉션 이미지 URL")
        String imageUrl,

        @Schema(description = "셀렉션 제목")
        String title,

        @Schema(description = "셀렉션 설명")
        String description,

        @Schema(description = "셀렉션 하위 상품 목록")
        List<HomeProductResponse> products
) {
}
