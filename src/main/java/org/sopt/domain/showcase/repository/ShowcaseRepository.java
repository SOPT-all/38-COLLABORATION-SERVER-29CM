package org.sopt.domain.showcase.repository;

import java.util.List;

import org.sopt.domain.showcase.domain.Showcase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShowcaseRepository extends JpaRepository<Showcase, Long> {

    @Query("SELECT s FROM Showcase s JOIN FETCH s.section sec WHERE s.isFeatured = true ORDER BY s.displayOrder ASC, s.id ASC")
    List<Showcase> findFeaturedAll();

    @Query("SELECT s FROM Showcase s JOIN FETCH s.section sec WHERE sec.theme = :theme AND s.isFeatured = true ORDER BY s.displayOrder ASC, s.id ASC")
    List<Showcase> findFeaturedByTheme(@Param("theme") String theme);

    @Query("SELECT s FROM Showcase s JOIN FETCH s.section sec ORDER BY s.displayOrder DESC, s.id DESC")
    List<Showcase> findLastN(Pageable pageable);

    @Query("SELECT s FROM Showcase s JOIN FETCH s.section sec WHERE sec.theme = :theme ORDER BY s.displayOrder DESC, s.id DESC")
    List<Showcase> findLastNByTheme(@Param("theme") String theme, Pageable pageable);

    @Query("""
            SELECT s FROM Showcase s JOIN FETCH s.section sec
            ORDER BY sec.displayOrder ASC, sec.id ASC, s.displayOrder ASC, s.id ASC
            """)
    List<Showcase> findFirstPage(Pageable pageable);

    @Query("""
            SELECT s FROM Showcase s JOIN FETCH s.section sec
            WHERE sec.theme = :theme
            ORDER BY sec.displayOrder ASC, sec.id ASC, s.displayOrder ASC, s.id ASC
            """)
    List<Showcase> findFirstPageByTheme(@Param("theme") String theme, Pageable pageable);

    @Query("""
            SELECT s FROM Showcase s JOIN FETCH s.section sec
            WHERE (
                sec.displayOrder > :sectionDisplayOrder
                OR (sec.displayOrder = :sectionDisplayOrder AND sec.id > :sectionId)
                OR (sec.displayOrder = :sectionDisplayOrder AND sec.id = :sectionId AND s.displayOrder > :showcaseDisplayOrder)
                OR (sec.displayOrder = :sectionDisplayOrder AND sec.id = :sectionId AND s.displayOrder = :showcaseDisplayOrder AND s.id > :showcaseId)
            )
            ORDER BY sec.displayOrder ASC, sec.id ASC, s.displayOrder ASC, s.id ASC
            """)
    List<Showcase> findNextPage(
            @Param("sectionDisplayOrder") Integer sectionDisplayOrder,
            @Param("sectionId") Long sectionId,
            @Param("showcaseDisplayOrder") Integer showcaseDisplayOrder,
            @Param("showcaseId") Long showcaseId,
            Pageable pageable
    );

    @Query("""
            SELECT s FROM Showcase s JOIN FETCH s.section sec
            WHERE sec.theme = :theme
            AND (
                sec.displayOrder > :sectionDisplayOrder
                OR (sec.displayOrder = :sectionDisplayOrder AND sec.id > :sectionId)
                OR (sec.displayOrder = :sectionDisplayOrder AND sec.id = :sectionId AND s.displayOrder > :showcaseDisplayOrder)
                OR (sec.displayOrder = :sectionDisplayOrder AND sec.id = :sectionId AND s.displayOrder = :showcaseDisplayOrder AND s.id > :showcaseId)
            )
            ORDER BY sec.displayOrder ASC, sec.id ASC, s.displayOrder ASC, s.id ASC
            """)
    List<Showcase> findNextPageByTheme(
            @Param("theme") String theme,
            @Param("sectionDisplayOrder") Integer sectionDisplayOrder,
            @Param("sectionId") Long sectionId,
            @Param("showcaseDisplayOrder") Integer showcaseDisplayOrder,
            @Param("showcaseId") Long showcaseId,
            Pageable pageable
    );
}
