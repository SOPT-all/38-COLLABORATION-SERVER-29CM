package org.sopt.domain.product.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.product.code.ProductErrorCode;
import org.sopt.domain.product.domain.Product;
import org.sopt.domain.product.domain.ProductLike;
import org.sopt.domain.product.dto.response.ProductLikeToggleResponse;
import org.sopt.domain.product.repository.ProductLikeRepository;
import org.sopt.domain.product.repository.ProductRepository;
import org.sopt.domain.user.domain.User;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BaseException;
import org.sopt.global.support.viewer.ViewerType;
import org.sopt.global.support.viewer.ViewerTypeResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final long TEST_USER_ID = 1L;

    private final ViewerTypeResolver viewerTypeResolver;
    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProductLikeToggleResponse toggleLike(Long productId, String rawViewerType) {
        ViewerType viewerType = viewerTypeResolver.resolve(rawViewerType);

        if (viewerType == ViewerType.GUEST) {
            throw new BaseException(ProductErrorCode.LOGIN_REQUIRED);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BaseException(ProductErrorCode.PRODUCT_NOT_FOUND));

        Optional<ProductLike> existingLike = productLikeRepository.findByUserIdAndProductId(TEST_USER_ID, productId);

        boolean isLiked;
        if (existingLike.isPresent()) {
            productLikeRepository.delete(existingLike.get());
            product.decreaseLikeCount();
            isLiked = false;
        } else {
            User user = userRepository.findById(TEST_USER_ID)
                    .orElseThrow(() -> new BaseException(GlobalErrorCode.INTERNAL_SERVER_ERROR));
            productLikeRepository.save(ProductLike.of(user, product));
            product.increaseLikeCount();
            isLiked = true;
        }

        return new ProductLikeToggleResponse(productId, isLiked, product.getLikeCount());
    }
}
