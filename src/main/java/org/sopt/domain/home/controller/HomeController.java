package org.sopt.domain.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.home.dto.response.HomeMainResponse;
import org.sopt.domain.home.service.HomeService;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Home", description = "홈 API")
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 메인 조회", description = "홈 화면에 필요한 숏컷과 메인 큐레이션 섹션을 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonApiResponse<HomeMainResponse>> getHomeMain(
            @Parameter(description = "사용자 상태. user 또는 guest만 허용", example = "guest")
            @RequestParam(required = false) String viewerType,

            @Parameter(description = "다음 페이지 조회 기준 opaque cursor")
            @RequestParam(required = false) String cursor,

            @Parameter(description = "한 번에 조회할 메인 피드 섹션 개수. 최소 1, 최대 20", example = "5")
            @RequestParam(required = false) Integer size
    ) {
        HomeMainResponse response = homeService.getHomeMain(viewerType, cursor, size);
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }
}
