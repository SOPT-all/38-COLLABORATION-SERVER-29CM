package org.sopt.domain.product.repository;

import org.sopt.domain.product.domain.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
}
