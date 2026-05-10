package org.sopt.domain.notice.repository;

import java.util.List;

import org.sopt.domain.notice.domain.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByOrderByDisplayOrderDescIdDesc(Pageable pageable);
}
