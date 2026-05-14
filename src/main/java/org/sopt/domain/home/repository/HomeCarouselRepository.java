package org.sopt.domain.home.repository;

import java.util.List;

import org.sopt.domain.home.domain.HomeCarousel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeCarouselRepository extends JpaRepository<HomeCarousel, Long> {

    List<HomeCarousel> findAllByOrderByDisplayOrderAscIdAsc();
}
