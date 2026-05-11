package org.sopt.domain.category.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.category.domain.Category;
import org.sopt.domain.category.dto.response.MiddleCategoryResponse;
import org.sopt.domain.category.dto.response.NavCategoryResponse;
import org.sopt.domain.category.dto.response.SubCategoryResponse;
import org.sopt.domain.category.dto.response.TopCategoryResponse;
import org.sopt.domain.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private static final int TOP_DEPTH = 1;
    private static final int MIDDLE_DEPTH = 2;
    private static final int SUB_DEPTH = 3;

    private final CategoryRepository categoryRepository;

    public NavCategoryResponse getNavCategories() {
        List<Category> categories = categoryRepository.findAllForNav();
        Map<Long, List<Category>> childrenByParentId = groupChildrenByParentId(categories);

        List<TopCategoryResponse> topCategories = categories.stream()
                .filter(category -> category.getDepth() == TOP_DEPTH)
                .map(category -> toTopCategoryResponse(category, childrenByParentId))
                .toList();

        return new NavCategoryResponse(topCategories);
    }

    private Map<Long, List<Category>> groupChildrenByParentId(List<Category> categories) {
        return categories.stream()
                .filter(category -> category.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));
    }

    private TopCategoryResponse toTopCategoryResponse(
            Category category,
            Map<Long, List<Category>> childrenByParentId
    ) {
        List<MiddleCategoryResponse> middleCategories = childrenByParentId
                .getOrDefault(category.getId(), List.of())
                .stream()
                .filter(child -> child.getDepth() == MIDDLE_DEPTH)
                .map(child -> toMiddleCategoryResponse(child, childrenByParentId))
                .toList();

        return new TopCategoryResponse(
                category.getId(),
                category.getName(),
                middleCategories
        );
    }

    private MiddleCategoryResponse toMiddleCategoryResponse(
            Category category,
            Map<Long, List<Category>> childrenByParentId
    ) {
        List<SubCategoryResponse> subCategories = childrenByParentId
                .getOrDefault(category.getId(), List.of())
                .stream()
                .filter(child -> child.getDepth() == SUB_DEPTH)
                .map(this::toSubCategoryResponse)
                .toList();

        return new MiddleCategoryResponse(
                category.getId(),
                category.getName(),
                subCategories
        );
    }

    private SubCategoryResponse toSubCategoryResponse(Category category) {
        return new SubCategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}
