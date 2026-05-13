package org.sopt.domain.home.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.home.domain.HomeSection;
import org.sopt.domain.home.domain.HomeSelection;
import org.sopt.domain.home.domain.HomeShortcut;
import org.sopt.domain.home.domain.SelectionProduct;
import org.sopt.domain.home.dto.response.HomeMainResponse;
import org.sopt.domain.home.dto.response.HomeProductResponse;
import org.sopt.domain.home.dto.response.HomeSectionResponse;
import org.sopt.domain.home.dto.response.HomeSelectionResponse;
import org.sopt.domain.home.dto.response.HomeShortcutResponse;
import org.sopt.domain.home.repository.HomeSectionRepository;
import org.sopt.domain.home.repository.HomeSelectionRepository;
import org.sopt.domain.home.repository.HomeShortcutRepository;
import org.sopt.domain.home.repository.SelectionProductRepository;
import org.sopt.domain.home.support.HomeCursorPayload;
import org.sopt.domain.product.domain.Product;
import org.sopt.domain.product.domain.ProductTag;
import org.sopt.domain.product.repository.ProductLikeRepository;
import org.sopt.domain.product.repository.ProductTagRepository;
import org.sopt.global.support.cursor.CursorCodec;
import org.sopt.global.support.pagination.PageInfoResponse;
import org.sopt.global.support.pagination.PaginationValidator;
import org.sopt.global.support.viewer.ViewerType;
import org.sopt.global.support.viewer.ViewerTypeResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private static final int DEFAULT_SECTION_SIZE = 5;
    private static final int MIN_SECTION_SIZE = 1;
    private static final int MAX_SECTION_SIZE = 20;
    private static final long TEST_USER_ID = 1L;

    private final ViewerTypeResolver viewerTypeResolver;
    private final CursorCodec cursorCodec;
    private final HomeShortcutRepository homeShortcutRepository;
    private final HomeSectionRepository homeSectionRepository;
    private final HomeSelectionRepository homeSelectionRepository;
    private final SelectionProductRepository selectionProductRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductLikeRepository productLikeRepository;

    public HomeMainResponse getHomeMain(String rawViewerType, String rawCursor, Integer rawSize) {
        ViewerType viewerType = viewerTypeResolver.resolve(rawViewerType);
        int size = PaginationValidator.resolveAndValidateSize(
                rawSize,
                DEFAULT_SECTION_SIZE,
                MIN_SECTION_SIZE,
                MAX_SECTION_SIZE
        );
        HomeCursorPayload cursor = decodeCursor(rawCursor);

        List<HomeShortcutResponse> shortcuts = getShortcuts();
        List<HomeSection> fetchedSections = getSections(cursor, size);
        boolean hasNext = fetchedSections.size() > size;
        List<HomeSection> sections = fetchedSections.stream()
                .limit(size)
                .toList();

        List<HomeSectionResponse> sectionResponses = getSectionResponses(sections, viewerType);
        PageInfoResponse pageInfo = createPageInfo(sections, hasNext, size);

        return new HomeMainResponse(shortcuts, sectionResponses, pageInfo);
    }

    private HomeCursorPayload decodeCursor(String rawCursor) {
        if (rawCursor == null) {
            return null;
        }

        return cursorCodec.decode(rawCursor, HomeCursorPayload.class);
    }

    private List<HomeShortcutResponse> getShortcuts() {
        return homeShortcutRepository.findAllByOrderByDisplayOrderAscIdAsc()
                .stream()
                .map(this::toShortcutResponse)
                .toList();
    }

    private List<HomeSection> getSections(HomeCursorPayload cursor, int size) {
        Integer cursorDisplayOrder = cursor == null ? null : cursor.displayOrder();
        Long cursorSectionId = cursor == null ? null : cursor.sectionId();

        return homeSectionRepository.findPageAfterCursor(
                cursorDisplayOrder,
                cursorSectionId,
                PageRequest.of(0, size + 1)
        );
    }

    private List<HomeSectionResponse> getSectionResponses(List<HomeSection> sections, ViewerType viewerType) {
        if (sections.isEmpty()) {
            return List.of();
        }

        List<Long> sectionIds = sections.stream()
                .map(HomeSection::getId)
                .toList();
        List<HomeSelection> selections = homeSelectionRepository.findByHomeSectionIds(sectionIds);
        Map<Long, List<HomeSelection>> selectionsBySectionId = groupBy(
                selections,
                HomeSelection::getHomeSectionId
        );

        List<Long> selectionIds = selections.stream()
                .map(HomeSelection::getId)
                .toList();
        Map<Long, List<SelectionProduct>> selectionProductsBySelectionId = getSelectionProductsBySelectionId(
                selectionIds
        );
        Map<Long, List<String>> tagsByProductId = getTagsByProductId(selectionProductsBySelectionId.values());
        Set<Long> likedProductIds = getLikedProductIds(viewerType, selectionProductsBySelectionId.values());

        return sections.stream()
                .map(section -> toSectionResponse(
                        section,
                        selectionsBySectionId,
                        selectionProductsBySelectionId,
                        tagsByProductId,
                        likedProductIds
                ))
                .toList();
    }

    private Map<Long, List<SelectionProduct>> getSelectionProductsBySelectionId(List<Long> selectionIds) {
        if (selectionIds.isEmpty()) {
            return Map.of();
        }

        return groupBy(
                selectionProductRepository.findByHomeSelectionIdsWithProduct(selectionIds),
                SelectionProduct::getHomeSelectionId
        );
    }

    private Map<Long, List<String>> getTagsByProductId(Collection<List<SelectionProduct>> groupedSelectionProducts) {
        List<Long> productIds = getProductIds(groupedSelectionProducts);
        if (productIds.isEmpty()) {
            return Map.of();
        }

        return productTagRepository.findByProductIds(productIds)
                .stream()
                .collect(Collectors.groupingBy(
                        ProductTag::getProductId,
                        LinkedHashMap::new,
                        Collectors.mapping(ProductTag::getName, Collectors.toList())
                ));
    }

    private Set<Long> getLikedProductIds(
            ViewerType viewerType,
            Collection<List<SelectionProduct>> groupedSelectionProducts
    ) {
        if (viewerType == ViewerType.GUEST) {
            return Set.of();
        }

        List<Long> productIds = getProductIds(groupedSelectionProducts);
        if (productIds.isEmpty()) {
            return Set.of();
        }

        return productLikeRepository.findLikedProductIds(TEST_USER_ID, productIds);
    }

    private List<Long> getProductIds(Collection<List<SelectionProduct>> groupedSelectionProducts) {
        return groupedSelectionProducts.stream()
                .flatMap(List::stream)
                .map(SelectionProduct::getProductId)
                .distinct()
                .toList();
    }

    private HomeSectionResponse toSectionResponse(
            HomeSection section,
            Map<Long, List<HomeSelection>> selectionsBySectionId,
            Map<Long, List<SelectionProduct>> selectionProductsBySelectionId,
            Map<Long, List<String>> tagsByProductId,
            Set<Long> likedProductIds
    ) {
        List<HomeSelectionResponse> selections = selectionsBySectionId
                .getOrDefault(section.getId(), List.of())
                .stream()
                .map(selection -> toSelectionResponse(
                        selection,
                        selectionProductsBySelectionId,
                        tagsByProductId,
                        likedProductIds
                ))
                .toList();

        return new HomeSectionResponse(
                section.getId(),
                section.getTitle(),
                section.getDescription(),
                section.getHeroImageUrl(),
                selections
        );
    }

    private HomeSelectionResponse toSelectionResponse(
            HomeSelection selection,
            Map<Long, List<SelectionProduct>> selectionProductsBySelectionId,
            Map<Long, List<String>> tagsByProductId,
            Set<Long> likedProductIds
    ) {
        List<HomeProductResponse> products = selectionProductsBySelectionId
                .getOrDefault(selection.getId(), List.of())
                .stream()
                .map(selectionProduct -> toProductResponse(
                        selectionProduct,
                        tagsByProductId,
                        likedProductIds
                ))
                .toList();

        return new HomeSelectionResponse(
                selection.getId(),
                selection.getImageUrl(),
                selection.getTitle(),
                selection.getDescription(),
                products
        );
    }

    private HomeProductResponse toProductResponse(
            SelectionProduct selectionProduct,
            Map<Long, List<String>> tagsByProductId,
            Set<Long> likedProductIds
    ) {
        Product product = selectionProduct.getProduct();
        Long productId = product.getId();

        return new HomeProductResponse(
                productId,
                product.getImageUrl(),
                product.getBrandName(),
                product.getName(),
                product.getSaleRate(),
                product.getPrice(),
                tagsByProductId.getOrDefault(productId, List.of()),
                product.getLikeCount(),
                likedProductIds.contains(productId)
        );
    }

    private HomeShortcutResponse toShortcutResponse(HomeShortcut shortcut) {
        return new HomeShortcutResponse(
                shortcut.getId(),
                shortcut.getName(),
                shortcut.getImageUrl(),
                shortcut.getCategoryId()
        );
    }

    private PageInfoResponse createPageInfo(List<HomeSection> sections, boolean hasNext, int size) {
        String nextCursor = null;
        if (hasNext && !sections.isEmpty()) {
            HomeSection lastSection = sections.get(sections.size() - 1);
            nextCursor = cursorCodec.encode(new HomeCursorPayload(
                    lastSection.getDisplayOrder(),
                    lastSection.getId()
            ));
        }

        return new PageInfoResponse(nextCursor, hasNext, size);
    }

    private <T> Map<Long, List<T>> groupBy(List<T> values, Function<T, Long> classifier) {
        return values.stream()
                .collect(Collectors.groupingBy(
                        classifier,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }
}
