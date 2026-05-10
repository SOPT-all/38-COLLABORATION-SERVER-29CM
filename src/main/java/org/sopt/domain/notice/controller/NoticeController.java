package org.sopt.domain.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.notice.dto.response.NoticeResponse;
import org.sopt.domain.notice.service.NoticeService;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice", description = "공지 API")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "푸터 공지 조회", description = "Footer NOTICE 영역에 노출할 최신 공지 5개를 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonApiResponse<NoticeResponse>> getNotices() {
        NoticeResponse response = noticeService.getNotices();
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }
}
