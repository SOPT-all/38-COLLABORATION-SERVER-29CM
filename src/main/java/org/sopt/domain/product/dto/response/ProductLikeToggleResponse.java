package org.sopt.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 좋아요 토글 응답")
public record ProductLikeToggleResponse(
        @Schema(description = "상품 ID", example = "1001")
        Long productId,

        @Schema(description = "토글 후 좋아요 여부", example = "true")
        boolean isLiked,

        @Schema(description = "토글 후 좋아요 수", example = "101")
        int likeCount
) {
}
