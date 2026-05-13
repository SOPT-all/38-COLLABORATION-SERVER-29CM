package org.sopt.domain.product.repository;

import java.util.Collection;
import java.util.List;

import org.sopt.domain.product.domain.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {

    @Query("""
            select pt
            from ProductTag pt
            where pt.productId in :productIds
            order by pt.productId asc, pt.displayOrder asc, pt.id asc
            """)
    List<ProductTag> findByProductIds(@Param("productIds") Collection<Long> productIds);
}
