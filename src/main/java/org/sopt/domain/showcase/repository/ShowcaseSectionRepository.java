package org.sopt.domain.showcase.repository;

import org.sopt.domain.showcase.domain.ShowcaseSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowcaseSectionRepository extends JpaRepository<ShowcaseSection, Long> {

    boolean existsByTheme(String theme);
}
