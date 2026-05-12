package org.sopt.domain.product.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND("PRODUCT-E001", HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    LOGIN_REQUIRED("PRODUCT-E002", HttpStatus.FORBIDDEN, "로그인이 필요한 기능입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
