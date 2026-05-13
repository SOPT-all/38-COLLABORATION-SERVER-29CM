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
}
