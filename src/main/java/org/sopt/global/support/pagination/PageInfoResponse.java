package org.sopt.global.support.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커서 기반 페이지 정보 응답")
public record PageInfoResponse(
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(description = "다음 페이지 요청 시 전달할 cursor", nullable = true)
        String nextCursor,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "해당 요청에 실제 적용된 page size", example = "5")
        int size
) {
}
