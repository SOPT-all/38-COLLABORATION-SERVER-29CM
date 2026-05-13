package org.sopt.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.category.dto.response.NavCategoryResponse;
import org.sopt.domain.category.service.CategoryService;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Navigation", description = "네비게이션 API")
@RestController
@RequestMapping("/api/v1/nav")
@RequiredArgsConstructor
public class NavController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 메가 메뉴 조회", description = "Header 메가 메뉴에 노출할 카테고리 계층을 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonApiResponse<NavCategoryResponse>> getNavCategories() {
        NavCategoryResponse response = categoryService.getNavCategories();
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }
}
