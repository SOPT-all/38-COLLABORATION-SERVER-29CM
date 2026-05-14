package org.sopt.domain.showcase.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.showcase.cursor.ShowcaseCursorPayload;
import org.sopt.domain.showcase.domain.Showcase;
import org.sopt.domain.showcase.domain.ShowcaseSection;
import org.sopt.domain.showcase.dto.response.ShowcaseItemResponse;
import org.sopt.domain.showcase.dto.response.ShowcaseResponse;
import org.sopt.domain.showcase.dto.response.ShowcaseSectionResponse;
import org.sopt.domain.showcase.repository.ShowcaseRepository;
import org.sopt.domain.showcase.repository.ShowcaseSectionRepository;
import org.sopt.global.s3.service.S3Service;
import org.sopt.global.support.cursor.CursorCodec;
import org.sopt.global.support.pagination.PageInfoResponse;
import org.sopt.global.support.pagination.PaginationValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowcaseService {

    private static final int DEFAULT_SIZE = 3;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 5;

    private final ShowcaseRepository showcaseRepository;
    private final ShowcaseSectionRepository showcaseSectionRepository;
    private final CursorCodec cursorCodec;
    private final S3Service s3Service;

    public ShowcaseResponse getShowcases(String themeStr, String cursorStr, Integer rawSize) {
        int size = PaginationValidator.resolveAndValidateSize(rawSize, DEFAULT_SIZE, MIN_SIZE, MAX_SIZE);
        String theme = resolveTheme(themeStr);
        ShowcaseCursorPayload cursor = cursorStr != null
                ? cursorCodec.decode(cursorStr, ShowcaseCursorPayload.class)
                : null;
        Map<String, String> presignedUrlCache = new LinkedHashMap<>();

        List<ShowcaseItemResponse> featured = cursor == null
                ? fetchFeatured(theme, presignedUrlCache)
                : List.of();

        List<ShowcaseSection> fetchedSections = fetchSectionPage(theme, cursor, size + 1);

        boolean hasNext = fetchedSections.size() > size;
        List<ShowcaseSection> pageSections = hasNext ? fetchedSections.subList(0, size) : fetchedSections;

        List<Long> sectionIds = pageSections.stream().map(ShowcaseSection::getId).toList();
        List<Showcase> showcases = sectionIds.isEmpty()
                ? List.of()
                : showcaseRepository.findBySectionIds(sectionIds);

        String nextCursor = hasNext ? encodeSectionCursor(pageSections.get(pageSections.size() - 1)) : null;
        List<ShowcaseSectionResponse> sections = buildSectionResponses(pageSections, showcases, presignedUrlCache);

        return new ShowcaseResponse(featured, sections, new PageInfoResponse(nextCursor, hasNext, size));
    }

    private String resolveTheme(String themeStr) {
        if (themeStr == null || themeStr.isBlank()) {
            return null;
        }
        return themeStr.toUpperCase();
    }

    private List<ShowcaseItemResponse> fetchFeatured(String theme, Map<String, String> presignedUrlCache) {
        List<Showcase> items = theme != null
                ? showcaseRepository.findFeaturedByTheme(theme)
                : showcaseRepository.findFeaturedAll();

        if (items.isEmpty()) {
            List<Showcase> lastTwo = theme != null
                    ? showcaseRepository.findLastNByTheme(theme, PageRequest.of(0, 2))
                    : showcaseRepository.findLastN(PageRequest.of(0, 2));
            List<Showcase> reversed = new ArrayList<>(lastTwo);
            Collections.reverse(reversed);
            items = reversed;
        }

        return items.stream()
                .map(showcase -> toShowcaseItemResponse(showcase, presignedUrlCache))
                .toList();
    }

    private List<ShowcaseSection> fetchSectionPage(String theme, ShowcaseCursorPayload cursor, int limit) {
        PageRequest pageable = PageRequest.of(0, limit);
        if (cursor == null) {
            return theme != null
                    ? showcaseSectionRepository.findFirstPageByTheme(theme, pageable)
                    : showcaseSectionRepository.findFirstPage(pageable);
        }
        return theme != null
                ? showcaseSectionRepository.findNextPageByTheme(theme, cursor.lastDisplayOrder(), cursor.lastId(), pageable)
                : showcaseSectionRepository.findNextPage(cursor.lastDisplayOrder(), cursor.lastId(), pageable);
    }

    private String encodeSectionCursor(ShowcaseSection lastSection) {
        return cursorCodec.encode(new ShowcaseCursorPayload(
                lastSection.getDisplayOrder(),
                lastSection.getId()
        ));
    }

    private List<ShowcaseSectionResponse> buildSectionResponses(
            List<ShowcaseSection> sections,
            List<Showcase> showcases,
            Map<String, String> presignedUrlCache
    ) {
        Map<Long, List<Showcase>> bySectionId = showcases.stream()
                .collect(Collectors.groupingBy(s -> s.getSection().getId()));

        return sections.stream()
                .map(sec -> toSectionResponse(sec, bySectionId.getOrDefault(sec.getId(), List.of()), presignedUrlCache))
                .toList();
    }

    private ShowcaseSectionResponse toSectionResponse(
            ShowcaseSection section,
            List<Showcase> showcases,
            Map<String, String> presignedUrlCache
    ) {
        return new ShowcaseSectionResponse(
                section.getId(),
                section.getTheme(),
                section.getTitle(),
                showcases.stream()
                        .map(showcase -> toShowcaseItemResponse(showcase, presignedUrlCache))
                        .toList()
        );
    }

    private ShowcaseItemResponse toShowcaseItemResponse(
            Showcase showcase,
            Map<String, String> presignedUrlCache
    ) {
        return new ShowcaseItemResponse(
                showcase.getId(),
                showcase.getTitle(),
                showcase.getDescription(),
                presignImageUrl(showcase.getImageUrl(), presignedUrlCache),
                showcase.getStartDate(),
                showcase.getEndDate()
        );
    }

    private String presignImageUrl(String objectKey, Map<String, String> presignedUrlCache) {
        return presignedUrlCache.computeIfAbsent(objectKey, s3Service::generatePresignedUrlOrNull);
    }
}
