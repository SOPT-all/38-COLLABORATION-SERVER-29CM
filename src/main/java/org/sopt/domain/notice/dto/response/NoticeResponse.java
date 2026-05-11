package org.sopt.domain.notice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "푸터 공지 목록 응답")
public record NoticeResponse(
        @Schema(description = "푸터 공지 목록")
        List<NoticeItemResponse> notices
) {
}
