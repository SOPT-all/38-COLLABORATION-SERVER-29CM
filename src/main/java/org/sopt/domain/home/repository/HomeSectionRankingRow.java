package org.sopt.domain.home.repository;

import org.sopt.domain.home.domain.HomeSection;

public record HomeSectionRankingRow(
        HomeSection section,
        Long likedProductCount
) {
}
