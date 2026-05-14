package org.sopt.domain.home.service;

import java.util.Collection;
import java.util.Comparator;
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
import org.sopt.global.s3.service.S3Service;
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
    private final S3Service s3Service;

    public HomeMainResponse getHomeMain(String rawViewerType, String rawCursor, Integer rawSize) {
        ViewerType viewerType = viewerTypeResolver.resolve(rawViewerType);
        int size = PaginationValidator.resolveAndValidateSize(
                rawSize,
                DEFAULT_SECTION_SIZE,
                MIN_SECTION_SIZE,
                MAX_SECTION_SIZE
        );
        HomeCursorPayload cursor = decodeCursor(rawCursor, viewerType);
        Map<String, String> presignedUrlCache = new LinkedHashMap<>();

        List<HomeShortcutResponse> shortcuts = getShortcuts(presignedUrlCache);
        List<HomeSectionPageItem> fetchedSectionItems = getSectionPageItems(cursor, viewerType, size);
        boolean hasNext = fetchedSectionItems.size() > size;
        List<HomeSectionPageItem> sectionPageItems = fetchedSectionItems.stream()
                .limit(size)
                .toList();
        List<HomeSection> sections = sectionPageItems.stream()
                .map(HomeSectionPageItem::section)
                .toList();

        List<HomeSectionResponse> sectionResponses = getSectionResponses(sections, viewerType, presignedUrlCache);
        PageInfoResponse pageInfo = createPageInfo(sectionPageItems, hasNext, size, viewerType);

        return new HomeMainResponse(shortcuts, sectionResponses, pageInfo);
    }

    private HomeCursorPayload decodeCursor(String rawCursor, ViewerType viewerType) {
        if (rawCursor == null) {
            return null;
        }

        HomeCursorPayload cursor = cursorCodec.decode(rawCursor, HomeCursorPayload.class);
        cursor.validateViewerType(viewerType);
        return cursor;
    }

    private List<HomeShortcutResponse> getShortcuts(Map<String, String> presignedUrlCache) {
        return homeShortcutRepository.findAllByOrderByDisplayOrderAscIdAsc()
                .stream()
                .map(shortcut -> toShortcutResponse(shortcut, presignedUrlCache))
                .toList();
    }

    private List<HomeSectionPageItem> getSectionPageItems(HomeCursorPayload cursor, ViewerType viewerType, int size) {
        if (viewerType == ViewerType.USER) {
            return getUserSectionPageItems(cursor, size);
        }

        return getGuestSectionPageItems(cursor, size);
    }

    private List<HomeSectionPageItem> getGuestSectionPageItems(HomeCursorPayload cursor, int size) {
        Integer cursorDisplayOrder = cursor == null ? null : cursor.displayOrder();
        Long cursorSectionId = cursor == null ? null : cursor.sectionId();

        return homeSectionRepository.findPageAfterCursor(
                cursorDisplayOrder,
                cursorSectionId,
                PageRequest.of(0, size + 1)
        ).stream()
                .map(section -> new HomeSectionPageItem(section, null))
                .toList();
    }

    private List<HomeSectionPageItem> getUserSectionPageItems(HomeCursorPayload cursor, int size) {
        Long cursorLikedProductCount = cursor == null ? null : cursor.likedProductCount();
        Integer cursorDisplayOrder = cursor == null ? null : cursor.displayOrder();
        Long cursorSectionId = cursor == null ? null : cursor.sectionId();

        return homeSectionRepository.findUserPageAfterCursor(
                TEST_USER_ID,
                cursorLikedProductCount,
                cursorDisplayOrder,
                cursorSectionId,
                PageRequest.of(0, size + 1)
        ).stream()
                .map(row -> new HomeSectionPageItem(row.section(), row.likedProductCount()))
                .toList();
    }

    private List<HomeSectionResponse> getSectionResponses(
            List<HomeSection> sections,
            ViewerType viewerType,
            Map<String, String> presignedUrlCache
    ) {
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
        List<Long> productIds = getProductIds(selectionProductsBySelectionId.values());
        Map<Long, List<String>> tagsByProductId = getTagsByProductId(productIds);
        Set<Long> likedProductIds = getLikedProductIds(viewerType, productIds);

        return sections.stream()
                .map(section -> toSectionResponse(
                        section,
                        selectionsBySectionId,
                        selectionProductsBySelectionId,
                        tagsByProductId,
                        likedProductIds,
                        viewerType,
                        presignedUrlCache
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

    private Map<Long, List<String>> getTagsByProductId(List<Long> productIds) {
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
            List<Long> productIds
    ) {
        if (viewerType == ViewerType.GUEST) {
            return Set.of();
        }

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
            Set<Long> likedProductIds,
            ViewerType viewerType,
            Map<String, String> presignedUrlCache
    ) {
        List<HomeSelection> sortedSelections = sortSelections(
                selectionsBySectionId.getOrDefault(section.getId(), List.of()),
                selectionProductsBySelectionId,
                likedProductIds,
                viewerType
        );
        List<HomeSelectionResponse> selections = sortedSelections
                .stream()
                .map(selection -> toSelectionResponse(
                        selection,
                        selectionProductsBySelectionId,
                        tagsByProductId,
                        likedProductIds,
                        viewerType,
                        presignedUrlCache
                ))
                .toList();

        return new HomeSectionResponse(
                section.getId(),
                section.getTitle(),
                section.getDescription(),
                presignImageUrl(section.getHeroImageUrl(), presignedUrlCache),
                selections
        );
    }

    private HomeSelectionResponse toSelectionResponse(
            HomeSelection selection,
            Map<Long, List<SelectionProduct>> selectionProductsBySelectionId,
            Map<Long, List<String>> tagsByProductId,
            Set<Long> likedProductIds,
            ViewerType viewerType,
            Map<String, String> presignedUrlCache
    ) {
        List<SelectionProduct> sortedSelectionProducts = sortSelectionProducts(
                selectionProductsBySelectionId.getOrDefault(selection.getId(), List.of()),
                likedProductIds,
                viewerType
        );
        List<HomeProductResponse> products = sortedSelectionProducts
                .stream()
                .map(selectionProduct -> toProductResponse(
                        selectionProduct,
                        tagsByProductId,
                        likedProductIds,
                        presignedUrlCache
                ))
                .toList();

        return new HomeSelectionResponse(
                selection.getId(),
                presignImageUrl(selection.getImageUrl(), presignedUrlCache),
                selection.getTitle(),
                selection.getDescription(),
                products
        );
    }

    private List<HomeSelection> sortSelections(
            List<HomeSelection> selections,
            Map<Long, List<SelectionProduct>> selectionProductsBySelectionId,
            Set<Long> likedProductIds,
            ViewerType viewerType
    ) {
        Comparator<HomeSelection> comparator = Comparator
                .comparingInt(HomeSelection::getDisplayOrder)
                .thenComparing(HomeSelection::getId);

        if (viewerType == ViewerType.USER) {
            comparator = Comparator
                    .comparingLong((HomeSelection selection) -> countLikedProducts(
                            selection,
                            selectionProductsBySelectionId,
                            likedProductIds
                    ))
                    .reversed()
                    .thenComparingInt(HomeSelection::getDisplayOrder)
                    .thenComparing(HomeSelection::getId);
        }

        return selections.stream()
                .sorted(comparator)
                .toList();
    }

    private long countLikedProducts(
            HomeSelection selection,
            Map<Long, List<SelectionProduct>> selectionProductsBySelectionId,
            Set<Long> likedProductIds
    ) {
        return selectionProductsBySelectionId.getOrDefault(selection.getId(), List.of())
                .stream()
                .filter(selectionProduct -> likedProductIds.contains(selectionProduct.getProductId()))
                .count();
    }

    private List<SelectionProduct> sortSelectionProducts(
            List<SelectionProduct> selectionProducts,
            Set<Long> likedProductIds,
            ViewerType viewerType
    ) {
        Comparator<SelectionProduct> comparator = Comparator
                .comparingInt((SelectionProduct selectionProduct) -> selectionProduct.getProduct().getLikeCount())
                .reversed()
                .thenComparing(SelectionProduct::getProductId);

        if (viewerType == ViewerType.USER) {
            comparator = Comparator
                    .comparing((SelectionProduct selectionProduct) -> likedProductIds.contains(
                            selectionProduct.getProductId()
                    ))
                    .reversed()
                    .thenComparing(
                            Comparator.comparingInt(
                                    (SelectionProduct selectionProduct) -> selectionProduct.getProduct().getLikeCount()
                            ).reversed()
                    )
                    .thenComparing(SelectionProduct::getProductId);
        }

        return selectionProducts.stream()
                .sorted(comparator)
                .toList();
    }

    private HomeProductResponse toProductResponse(
            SelectionProduct selectionProduct,
            Map<Long, List<String>> tagsByProductId,
            Set<Long> likedProductIds,
            Map<String, String> presignedUrlCache
    ) {
        Product product = selectionProduct.getProduct();
        Long productId = product.getId();

        return new HomeProductResponse(
                productId,
                presignImageUrl(product.getImageUrl(), presignedUrlCache),
                product.getBrandName(),
                product.getName(),
                product.getSaleRate(),
                product.getPrice(),
                tagsByProductId.getOrDefault(productId, List.of()),
                product.getLikeCount(),
                likedProductIds.contains(productId)
        );
    }

    private HomeShortcutResponse toShortcutResponse(HomeShortcut shortcut, Map<String, String> presignedUrlCache) {
        return new HomeShortcutResponse(
                shortcut.getId(),
                shortcut.getName(),
                presignImageUrl(shortcut.getImageUrl(), presignedUrlCache),
                shortcut.getCategoryId()
        );
    }

    private String presignImageUrl(String objectKey, Map<String, String> presignedUrlCache) {
        return presignedUrlCache.computeIfAbsent(objectKey, s3Service::generatePresignedUrlOrNull);
    }

    private PageInfoResponse createPageInfo(
            List<HomeSectionPageItem> sectionPageItems,
            boolean hasNext,
            int size,
            ViewerType viewerType
    ) {
        String nextCursor = null;
        if (hasNext && !sectionPageItems.isEmpty()) {
            HomeSectionPageItem lastSectionPageItem = sectionPageItems.get(sectionPageItems.size() - 1);
            HomeSection lastSection = lastSectionPageItem.section();
            nextCursor = cursorCodec.encode(createCursorPayload(lastSectionPageItem, lastSection, viewerType));
        }

        return new PageInfoResponse(nextCursor, hasNext, size);
    }

    private HomeCursorPayload createCursorPayload(
            HomeSectionPageItem lastSectionPageItem,
            HomeSection lastSection,
            ViewerType viewerType
    ) {
        if (viewerType == ViewerType.USER) {
            return HomeCursorPayload.user(
                    lastSectionPageItem.likedProductCount(),
                    lastSection.getDisplayOrder(),
                    lastSection.getId()
            );
        }

        return HomeCursorPayload.guest(
                lastSection.getDisplayOrder(),
                lastSection.getId()
        );
    }

    private <T> Map<Long, List<T>> groupBy(List<T> values, Function<T, Long> classifier) {
        return values.stream()
                .collect(Collectors.groupingBy(
                        classifier,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private record HomeSectionPageItem(
            HomeSection section,
            Long likedProductCount
    ) {
    }
}
