package org.sopt.domain.showcase.repository;

import java.util.List;
import org.sopt.domain.showcase.domain.ShowcaseSection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShowcaseSectionRepository extends JpaRepository<ShowcaseSection, Long> {

    @Query("SELECT sec FROM ShowcaseSection sec ORDER BY sec.displayOrder ASC, sec.id ASC")
    List<ShowcaseSection> findFirstPage(Pageable pageable);

    @Query("SELECT sec FROM ShowcaseSection sec WHERE sec.theme = :theme ORDER BY sec.displayOrder ASC, sec.id ASC")
    List<ShowcaseSection> findFirstPageByTheme(@Param("theme") String theme, Pageable pageable);

    @Query("""
            SELECT sec FROM ShowcaseSection sec
            WHERE (sec.displayOrder > :lastDisplayOrder OR (sec.displayOrder = :lastDisplayOrder AND sec.id > :lastId))
            ORDER BY sec.displayOrder ASC, sec.id ASC
            """)
    List<ShowcaseSection> findNextPage(
            @Param("lastDisplayOrder") Integer lastDisplayOrder,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("""
            SELECT sec FROM ShowcaseSection sec
            WHERE sec.theme = :theme
            AND (sec.displayOrder > :lastDisplayOrder OR (sec.displayOrder = :lastDisplayOrder AND sec.id > :lastId))
            ORDER BY sec.displayOrder ASC, sec.id ASC
            """)
    List<ShowcaseSection> findNextPageByTheme(
            @Param("theme") String theme,
            @Param("lastDisplayOrder") Integer lastDisplayOrder,
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}
