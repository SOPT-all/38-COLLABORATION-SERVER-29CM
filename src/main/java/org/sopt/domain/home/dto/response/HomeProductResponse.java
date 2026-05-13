package org.sopt.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "홈 상품 카드 응답")
public record HomeProductResponse(
        @Schema(description = "상품 ID", example = "1001")
        Long productId,

        @Schema(description = "상품 이미지 URL")
        String imageUrl,

        @Schema(description = "브랜드명", example = "노티아")
        String brandName,

        @Schema(description = "상품명", example = "COTTON TWO TUCK PANTS - BLACK")
        String name,

        @Schema(description = "할인율. 할인 없음이면 0", example = "29")
        int saleRate,

        @Schema(description = "최종 판매가", example = "83250")
        int price,

        @Schema(description = "상품 카드 태그 목록")
        List<String> tags,

        @Schema(description = "상품 좋아요 수", example = "35000")
        int likeCount,

        @Schema(description = "상품 좋아요 여부", example = "false")
        boolean isLiked
) {
}
