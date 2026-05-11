package org.sopt.domain.notice.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.notice.domain.Notice;
import org.sopt.domain.notice.dto.response.NoticeItemResponse;
import org.sopt.domain.notice.dto.response.NoticeResponse;
import org.sopt.domain.notice.repository.NoticeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private static final int FOOTER_NOTICE_SIZE = 5;

    private final NoticeRepository noticeRepository;

    public NoticeResponse getNotices() {
        List<NoticeItemResponse> notices = noticeRepository
                .findFooterNotices(PageRequest.of(0, FOOTER_NOTICE_SIZE))
                .stream()
                .map(this::toNoticeItemResponse)
                .toList();

        return new NoticeResponse(notices);
    }

    private NoticeItemResponse toNoticeItemResponse(Notice notice) {
        return new NoticeItemResponse(
                notice.getId(),
                notice.getTitle()
        );
    }
}
