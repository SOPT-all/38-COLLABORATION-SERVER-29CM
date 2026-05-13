package org.sopt.domain.notice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "푸터 공지 항목 응답")
public record NoticeItemResponse(
        @Schema(description = "공지 ID", example = "1")
        Long noticeId,

        @Schema(description = "공지 제목", example = "공지 제목")
        String title
) {
}
