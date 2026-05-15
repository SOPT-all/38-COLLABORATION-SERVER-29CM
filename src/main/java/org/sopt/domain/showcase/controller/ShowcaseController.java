package org.sopt.domain.showcase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.showcase.dto.response.ShowcaseResponse;
import org.sopt.domain.showcase.service.ShowcaseService;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Showcase", description = "쇼케이스 API")
@RestController
@RequestMapping("/api/v1/showcases")
@RequiredArgsConstructor
public class ShowcaseController {

    private final ShowcaseService showcaseService;

    @Operation(
            summary = "쇼케이스 피드 조회",
            description = "ShowCase 화면의 상단 쇼케이스 배너와 테마별 쇼케이스 피드 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<CommonApiResponse<ShowcaseResponse>> getShowcases(
            @Parameter(description = "테마 필터", example = "LIFESTYLE")
            @RequestParam(required = false) String theme,

            @Parameter(description = "다음 페이지 조회 기준 cursor")
            @RequestParam(required = false) String cursor,

            @Parameter(description = "한 번에 조회할 쇼케이스 개수 (1~50, 기본값 12)")
            @RequestParam(required = false) Integer size
    ) {
        ShowcaseResponse response = showcaseService.getShowcases(theme, cursor, size);
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }
}
