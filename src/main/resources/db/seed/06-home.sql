-- 임시 더미데이터. 기획 전달 JSON 수신 후 교체 예정.
INSERT INTO home_shortcuts (id, category_id, name, image_url, display_order)
VALUES (1, 1, 'BEST', 'https://example.com/home/shortcut-best.png', 1),
       (2, 2, 'WOMEN', 'https://example.com/home/shortcut-women.png', 2),
       (3, 3, 'MEN', 'https://example.com/home/shortcut-men.png', 3),
       (4, 4, 'INTERIOR', 'https://example.com/home/shortcut-interior.png', 4),
       (5, 8, 'BEAUTY', 'https://example.com/home/shortcut-beauty.png', 5),
       (6, 11, 'KIDS', 'https://example.com/home/shortcut-kids.png', 6);

ALTER TABLE home_shortcuts AUTO_INCREMENT = 7;

INSERT INTO home_sections (id, title, description, hero_image_url, display_order)
VALUES (1, '매일 스며드는 스타일', '일상에 자연스럽게 스며드는 옷', 'https://example.com/home/section-1-main.png', 1),
       (2, '여름을 준비하는 방식', '가볍고 선명한 계절의 선택', 'https://example.com/home/section-2-main.png', 2),
       (3, '집 안의 작은 변화', '공간의 분위기를 바꾸는 오브제', 'https://example.com/home/section-3-main.png', 3),
       (4, '아이를 위한 부드러운 하루', '편안한 움직임을 위한 키즈 셀렉션', 'https://example.com/home/section-4-main.png', 4),
       (5, '매일 쓰는 감각', '생활에 오래 남는 작은 물건들', 'https://example.com/home/section-5-main.png', 5),
       (6, '차분한 기록의 시간', '책상 위에 두고 싶은 문구 셀렉션', 'https://example.com/home/section-6-main.png', 6);

ALTER TABLE home_sections AUTO_INCREMENT = 7;

INSERT INTO home_selections (id, home_section_id, image_url, title, description, display_order)
VALUES (11, 1, 'https://example.com/home/selection-11.png', '왕수빈이 고른 노티아', '편안함 속에 깃든 노티아의 스타일을 만나요.', 1),
       (12, 1, 'https://example.com/home/selection-12.png', '루트파인더의 여름', '유연한 실루엣으로 준비하는 계절입니다.', 2),
       (21, 2, 'https://example.com/home/selection-21.png', '가벼운 셔츠와 팬츠', '여름 옷장의 기본을 다시 고릅니다.', 1),
       (22, 2, 'https://example.com/home/selection-22.png', '데일리 케어 루틴', '매일 손이 가는 산뜻한 케어 아이템입니다.', 2),
       (31, 3, 'https://example.com/home/selection-31.png', '테이블 위의 오브제', '식탁에 차분한 감각을 더합니다.', 1),
       (41, 4, 'https://example.com/home/selection-41.png', '로토토베베 키즈웨어', '아이의 하루에 맞춘 편안한 선택입니다.', 1),
       (51, 5, 'https://example.com/home/selection-51.png', '매일 쓰는 생활용품', '욕실과 주방에 놓기 좋은 물건입니다.', 1),
       (61, 6, 'https://example.com/home/selection-61.png', '스튜디오노트 문구', '기록하는 시간을 위한 데스크 아이템입니다.', 1);

ALTER TABLE home_selections AUTO_INCREMENT = 62;

INSERT INTO selection_products (id, home_selection_id, product_id, display_order)
VALUES (1, 11, 1001, 1),
       (2, 11, 1002, 2),
       (3, 11, 1004, 3),
       (4, 12, 1003, 1),
       (5, 12, 1004, 2),
       (6, 12, 1002, 3),
       (7, 21, 1002, 1),
       (8, 21, 1001, 2),
       (9, 21, 1003, 3),
       (10, 22, 1009, 1),
       (11, 22, 1010, 2),
       (12, 31, 1007, 1),
       (13, 31, 1008, 2),
       (14, 41, 1005, 1),
       (15, 41, 1006, 2),
       (16, 51, 1009, 1),
       (17, 51, 1008, 2),
       (18, 61, 1011, 1),
       (19, 61, 1012, 2);

ALTER TABLE selection_products AUTO_INCREMENT = 20;
