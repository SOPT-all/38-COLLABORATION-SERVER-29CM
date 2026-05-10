package org.sopt.domain.category.repository;

import java.util.List;

import org.sopt.domain.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
            select c
            from Category c
            order by c.displayOrder asc, c.id asc
            """)
    List<Category> findAllForNav();
}
