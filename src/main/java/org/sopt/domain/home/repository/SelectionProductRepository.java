package org.sopt.domain.home.repository;

import java.util.Collection;
import java.util.List;

import org.sopt.domain.home.domain.SelectionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SelectionProductRepository extends JpaRepository<SelectionProduct, Long> {

    @Query("""
            select sp
            from SelectionProduct sp
            join fetch sp.product p
            where sp.homeSelectionId in :selectionIds
            order by sp.homeSelectionId asc, p.likeCount desc, sp.displayOrder asc, sp.id asc
            """)
    List<SelectionProduct> findByHomeSelectionIdsWithProduct(@Param("selectionIds") Collection<Long> selectionIds);
}
