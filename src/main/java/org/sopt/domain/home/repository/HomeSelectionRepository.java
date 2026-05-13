package org.sopt.domain.home.repository;

import java.util.Collection;
import java.util.List;

import org.sopt.domain.home.domain.HomeSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HomeSelectionRepository extends JpaRepository<HomeSelection, Long> {

    @Query("""
            select hs
            from HomeSelection hs
            where hs.homeSectionId in :sectionIds
            order by hs.homeSectionId asc, hs.displayOrder asc, hs.id asc
            """)
    List<HomeSelection> findByHomeSectionIds(@Param("sectionIds") Collection<Long> sectionIds);
}
