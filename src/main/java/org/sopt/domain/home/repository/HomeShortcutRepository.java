package org.sopt.domain.home.repository;

import java.util.List;

import org.sopt.domain.home.domain.HomeShortcut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeShortcutRepository extends JpaRepository<HomeShortcut, Long> {

    List<HomeShortcut> findAllByOrderByDisplayOrderAscIdAsc();
}
