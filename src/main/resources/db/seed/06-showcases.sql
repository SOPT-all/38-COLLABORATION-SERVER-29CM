INSERT INTO showcase_sections (id, theme, title, display_order)
VALUES (1, 'FASHION', '감도 높은 패션', 1),
       (2, 'LIVING',  '취향의 공간',   2),
       (3, 'BEAUTY',  '나를 위한 뷰티', 3);

INSERT INTO showcases (id, showcase_section_id, title, description, image_url, start_date, end_date, display_order, is_featured)
VALUES
    -- FASHION
    (1, 1, '2026 S/S 트렌드',  '봄 여름 시즌 트렌드 아이템 모음',   'https://example.com/showcase/1.png', '2026-03-01', '2026-05-31', 1, true),
    (2, 1, '린넨의 계절',       '시원한 린넨 소재 모음',              'https://example.com/showcase/2.png', '2026-04-01', '2026-07-31', 2, false),
    (3, 1, '데님 에디션',       '베이직 데님 큐레이션',               'https://example.com/showcase/3.png', '2026-01-01', '2026-12-31', 3, false),
    -- LIVING
    (4, 2, '화이트 리빙',       '화이트 톤 리빙 제품 모음',           'https://example.com/showcase/4.png', '2026-01-01', '2026-12-31', 1, true),
    (5, 2, '내추럴 키친',       '친환경 주방용품 큐레이션',            'https://example.com/showcase/5.png', '2026-02-01', '2026-11-30', 2, false),
    (6, 2, '스터디 코너',       '홈 오피스 소품 모음',                'https://example.com/showcase/6.png', '2026-01-01', '2026-12-31', 3, false),
    -- BEAUTY
    (7, 3, '클린 뷰티',         '성분 안심 뷰티 큐레이션',            'https://example.com/showcase/7.png', '2026-01-01', '2026-12-31', 1, false),
    (8, 3, '썸머 케어',         '여름 피부 관리 루틴',                'https://example.com/showcase/8.png', '2026-05-01', '2026-08-31', 2, false),
    (9, 3, '향기 컬렉션',       '프래그런스 아이템 큐레이션',          'https://example.com/showcase/9.png', '2026-01-01', '2026-12-31', 3, false);
