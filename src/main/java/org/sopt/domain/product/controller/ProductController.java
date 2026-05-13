package org.sopt.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.product.dto.response.ProductLikeToggleResponse;
import org.sopt.domain.product.service.ProductService;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 좋아요 토글", description = "상품 카드의 좋아요 버튼을 눌렀을 때 좋아요 상태를 토글합니다.")
    @PatchMapping("/{productId}/like")
    public ResponseEntity<CommonApiResponse<ProductLikeToggleResponse>> toggleLike(
            @Parameter(description = "좋아요 토글 대상 상품 ID", example = "1001")
            @PathVariable Long productId,

            @Parameter(description = "클라이언트 방문자 상태. user 또는 guest만 허용", example = "user")
            @RequestParam(required = false) String viewerType
    ) {
        ProductLikeToggleResponse response = productService.toggleLike(productId, viewerType);
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }
}
