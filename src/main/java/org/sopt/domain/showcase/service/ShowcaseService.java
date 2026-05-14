package org.sopt.domain.showcase.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.showcase.cursor.ShowcaseCursorPayload;
import org.sopt.domain.showcase.domain.Showcase;
import org.sopt.domain.showcase.domain.ShowcaseSection;
import org.sopt.domain.showcase.dto.response.ShowcaseItemResponse;
import org.sopt.domain.showcase.dto.response.ShowcaseResponse;
import org.sopt.domain.showcase.dto.response.ShowcaseSectionResponse;
import org.sopt.domain.showcase.repository.ShowcaseRepository;
import org.sopt.domain.showcase.repository.ShowcaseSectionRepository;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BaseException;
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

    private static final int DEFAULT_SIZE = 12;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 50;

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

        List<Showcase> fetchedShowcases = fetchShowcasePage(theme, cursor, size + 1);

        boolean hasNext = fetchedShowcases.size() > size;
        List<Showcase> pageShowcases = hasNext ? fetchedShowcases.subList(0, size) : fetchedShowcases;

        String nextCursor = hasNext ? encodeShowcaseCursor(pageShowcases.get(pageShowcases.size() - 1)) : null;
        List<ShowcaseSectionResponse> sections = buildSectionResponses(pageShowcases, presignedUrlCache);

        return new ShowcaseResponse(featured, sections, new PageInfoResponse(nextCursor, hasNext, size));
    }

    private String resolveTheme(String themeStr) {
        if (themeStr == null || themeStr.isBlank()) {
            return null;
        }
        if (!showcaseSectionRepository.existsByTheme(themeStr)) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
        return themeStr;
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

    private List<Showcase> fetchShowcasePage(String theme, ShowcaseCursorPayload cursor, int limit) {
        PageRequest pageable = PageRequest.of(0, limit);
        if (cursor == null) {
            return theme != null
                    ? showcaseRepository.findFirstPageByTheme(theme, pageable)
                    : showcaseRepository.findFirstPage(pageable);
        }
        return theme != null
                ? showcaseRepository.findNextPageByTheme(theme,
                        cursor.sectionDisplayOrder(), cursor.sectionId(),
                        cursor.showcaseDisplayOrder(), cursor.showcaseId(), pageable)
                : showcaseRepository.findNextPage(
                        cursor.sectionDisplayOrder(), cursor.sectionId(),
                        cursor.showcaseDisplayOrder(), cursor.showcaseId(), pageable);
    }

    private String encodeShowcaseCursor(Showcase lastShowcase) {
        return cursorCodec.encode(new ShowcaseCursorPayload(
                lastShowcase.getSection().getDisplayOrder(),
                lastShowcase.getSection().getId(),
                lastShowcase.getDisplayOrder(),
                lastShowcase.getId()
        ));
    }

    private List<ShowcaseSectionResponse> buildSectionResponses(
            List<Showcase> showcases,
            Map<String, String> presignedUrlCache
    ) {
        Map<Long, ShowcaseSection> sectionById = new LinkedHashMap<>();
        Map<Long, List<Showcase>> showcasesBySectionId = new LinkedHashMap<>();

        for (Showcase showcase : showcases) {
            ShowcaseSection section = showcase.getSection();
            sectionById.putIfAbsent(section.getId(), section);
            showcasesBySectionId.computeIfAbsent(section.getId(), k -> new ArrayList<>()).add(showcase);
        }

        return sectionById.values().stream()
                .map(sec -> toSectionResponse(sec, showcasesBySectionId.get(sec.getId()), presignedUrlCache))
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
