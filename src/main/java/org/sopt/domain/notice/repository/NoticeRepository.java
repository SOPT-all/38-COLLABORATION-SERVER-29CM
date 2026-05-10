package org.sopt.domain.notice.repository;

import java.util.List;

import org.sopt.domain.notice.domain.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("select n from Notice n order by n.displayOrder desc, n.id desc")
    List<Notice> findFooterNotices(Pageable pageable);
}
