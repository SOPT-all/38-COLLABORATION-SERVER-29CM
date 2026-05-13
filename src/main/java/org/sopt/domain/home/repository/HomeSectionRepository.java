package org.sopt.domain.home.repository;

import java.util.List;

import org.sopt.domain.home.domain.HomeSection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HomeSectionRepository extends JpaRepository<HomeSection, Long> {

    @Query("""
            select hs
            from HomeSection hs
            where (:cursorDisplayOrder is null
                or hs.displayOrder > :cursorDisplayOrder
                or (hs.displayOrder = :cursorDisplayOrder and hs.id > :cursorSectionId))
            order by hs.displayOrder asc, hs.id asc
            """)
    List<HomeSection> findPageAfterCursor(
            @Param("cursorDisplayOrder") Integer cursorDisplayOrder,
            @Param("cursorSectionId") Long cursorSectionId,
            Pageable pageable
    );

    @Query("""
            select new org.sopt.domain.home.repository.HomeSectionRankingRow(
                hs,
                count(distinct pl.productId)
            )
            from HomeSection hs
            left join HomeSelection hsl on hsl.homeSection = hs
            left join SelectionProduct sp on sp.homeSelection = hsl
            left join ProductLike pl on pl.product = sp.product
                and pl.userId = :userId
            group by hs
            having (:cursorLikedProductCount is null
                or count(distinct pl.productId) < :cursorLikedProductCount
                or (count(distinct pl.productId) = :cursorLikedProductCount
                    and (hs.displayOrder > :cursorDisplayOrder
                        or (hs.displayOrder = :cursorDisplayOrder and hs.id > :cursorSectionId))))
            order by count(distinct pl.productId) desc, hs.displayOrder asc, hs.id asc
            """)
    List<HomeSectionRankingRow> findUserPageAfterCursor(
            @Param("userId") Long userId,
            @Param("cursorLikedProductCount") Long cursorLikedProductCount,
            @Param("cursorDisplayOrder") Integer cursorDisplayOrder,
            @Param("cursorSectionId") Long cursorSectionId,
            Pageable pageable
    );
}
