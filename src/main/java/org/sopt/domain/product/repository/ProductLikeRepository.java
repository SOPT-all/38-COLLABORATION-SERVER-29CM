package org.sopt.domain.product.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.sopt.domain.product.domain.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);

    @Query("""
            select pl.productId
            from ProductLike pl
            where pl.userId = :userId
                and pl.productId in :productIds
            """)
    Set<Long> findLikedProductIds(
            @Param("userId") Long userId,
            @Param("productIds") Collection<Long> productIds
    );
}
