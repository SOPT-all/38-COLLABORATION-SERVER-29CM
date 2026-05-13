-- Carousels / Shortcuts / Sections / Selections / Selection-Products

-- home_carousels (10) — 캐러셀 엔티티/테이블 추가 후 주석 해제 예정
-- INSERT INTO home_carousels (id, image_url, alt_text, display_order)
-- VALUES
--     (1, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_01.png', 'SUMMER SIGNAL', 1),
--     (2, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_02.jpg', '수요입점회', 2),
--     (3, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_03.jpg', 'NIKE', 3),
--     (4, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_04.jpg', 'RONRON', 4),
--     (5, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_05.jpg', 'EASTLOGUE PERMANENT×MAZIUNTITLED', 5),
--     (6, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_06.jpg', 'FINCA', 6),
--     (7, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_07.jpg', 'KEEN', 7),
--     (8, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_08.jpg', 'AERSE', 8),
--     (9, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_09.png', '714CENTER', 9),
--     (10, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/carousel_10.png', 'SNOWPEAK APPAREL', 10);

-- home_shortcuts (10)
INSERT INTO home_shortcuts (id, category_id, name, image_url, display_order)
VALUES
    (1, 3, 'BEST', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/best.png', 1),
    (2, 2, 'MEN', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/men.png', 2),
    (3, 12, 'CULTURE', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/culture.png', 3),
    (4, 5, 'KITCHEN', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/kitchen.png', 4),
    (5, 6, 'ELECTRONICS', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/electronics.png', 5),
    (6, 7, 'DIGITAL', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/digital.png', 6),
    (7, 8, 'BEAUTY', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/beauty.png', 7),
    (8, 9, 'FOOD', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/food.png', 8),
    (9, 10, 'LEISURE', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/leisure.png', 9),
    (10, 11, 'KIDS', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/kids.png', 10);

-- home_sections (14)
INSERT INTO home_sections (id, title, description, hero_image_url, display_order)
VALUES
    (1, '문화적 영감의 파편', '절제된 그래픽에 담아낸 사파리스팟과 1011 갤러리의 독창적 아카이브를 소개해요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_01.jpg', 1),
    (2, '제현경의 추천', '셔츠, 드레스, 데님까지. 패션 인플루언서 제현경이 로우클래식의 감각적인 여름을 소개해요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_02.jpg', 2),
    (3, '시간이 빚어낸', '절제된 실루엣과 차분한 톤으로 완성하는 여름 컬렉션을 만나보세요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_03.jpg', 3),
    (4, '절제된 미래', '언어펙티드와 프로그레스의 세 번째 협업 컬렉션을 소개해요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_04.jpg', 4),
    (5, '손끝에서 피어난', '소일베이커의 아름다운 감성으로 일상을 채워요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_05.jpg', 5),
    (6, '나폴리의 산들바람', '스치는 바람처럼 여유로운 여름을 담은 지노키오의 여름 컬렉션을 같이 구경해요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_06.jpg', 6),
    (7, '여름을 신어요', '버켄스탁의 새로운 실루엣과 함께 여름 스타일을 완성해요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_07.jpg', 7),
    (8, '쿨링 시스템', '아이즈매거진의 시선으로 기록한 비브비브를 만나보세요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_08.jpg', 8),
    (9, '도시와 산을 잇는', '일상과 산행을 아우르는 아웃도어 상품을 특별 혜택으로 소개해요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_09.jpg', 9),
    (10, '시간을 입는 방법', '기본에 충실한 브론슨을 만나요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_10.jpg', 10),
    (11, '선물하기 좋은 시간', 'Apple Watch를 특별 혜택으로 만나요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_11.jpg', 11),
    (12, '특별한 관리', '오하입으로 특별한 헤어 관리를 시작하세요. 특별 혜택과 함께.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_12.jpg', 12),
    (13, '특별한 기회', '29'' 머들 퍼퓸 14ml 출시 기념 단독 혜택을 만나보세요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_13.jpg', 13),
    (14, '견고함을 담은', '레드윙의 인기 부츠 컬렉션을 소개해요.', 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_14.jpg', 14);

-- home_selections (14)
INSERT INTO home_selections (id, home_section_id, image_url, title, description, display_order)
VALUES
    (1, 1, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_01.jpg', '문화적 영감의 파편', '절제된 그래픽에 담아낸 사파리스팟과 1011 갤러리의 독창적 아카이브를 소개해요.', 1),
    (2, 2, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_02.jpg', '제현경의 추천', '셔츠, 드레스, 데님까지. 패션 인플루언서 제현경이 로우클래식의 감각적인 여름을 소개해요.', 1),
    (3, 3, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_03.jpg', '시간이 빚어낸', '절제된 실루엣과 차분한 톤으로 완성하는 여름 컬렉션을 만나보세요.', 1),
    (4, 4, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_04.jpg', '절제된 미래', '언어펙티드와 프로그레스의 세 번째 협업 컬렉션을 소개해요.', 1),
    (5, 5, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_05.jpg', '손끝에서 피어난', '소일베이커의 아름다운 감성으로 일상을 채워요.', 1),
    (6, 6, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_06.jpg', '나폴리의 산들바람', '스치는 바람처럼 여유로운 여름을 담은 지노키오의 여름 컬렉션을 같이 구경해요.', 1),
    (7, 7, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_07.jpg', '여름을 신어요', '버켄스탁의 새로운 실루엣과 함께 여름 스타일을 완성해요.', 1),
    (8, 8, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_08.jpg', '쿨링 시스템', '아이즈매거진의 시선으로 기록한 비브비브를 만나보세요.', 1),
    (9, 9, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_09.jpg', '도시와 산을 잇는', '일상과 산행을 아우르는 아웃도어 상품을 특별 혜택으로 소개해요.', 1),
    (10, 10, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_10.jpg', '시간을 입는 방법', '기본에 충실한 브론슨을 만나요.', 1),
    (11, 11, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_11.jpg', '선물하기 좋은 시간', 'Apple Watch를 특별 혜택으로 만나요.', 1),
    (12, 12, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_12.jpg', '특별한 관리', '오하입으로 특별한 헤어 관리를 시작하세요. 특별 혜택과 함께.', 1),
    (13, 13, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_13.jpg', '특별한 기회', '29'' 머들 퍼퓸 14ml 출시 기념 단독 혜택을 만나보세요.', 1),
    (14, 14, 'https://collaboration-29cm-prod-398050108626-ap-northeast-2-an.s3.ap-northeast-2.amazonaws.com/home/section_14.jpg', '견고함을 담은', '레드윙의 인기 부츠 컬렉션을 소개해요.', 1);

-- selection_products (41)
INSERT INTO selection_products (id, home_selection_id, product_id, display_order)
VALUES
    (1, 1, 3976683, 1),
    (2, 1, 3976694, 2),
    (3, 1, 3976696, 3),
    (4, 2, 3880226, 1),
    (5, 2, 3880394, 2),
    (6, 2, 3880579, 3),
    (7, 3, 3956948, 1),
    (8, 3, 3956953, 2),
    (9, 3, 3956954, 3),
    (10, 4, 2095859, 1),
    (11, 4, 3271567, 2),
    (12, 5, 112034, 1),
    (13, 5, 1564626, 2),
    (14, 5, 3297929, 3),
    (15, 6, 3966478, 1),
    (16, 6, 3966598, 2),
    (17, 6, 3966619, 3),
    (18, 7, 3761535, 1),
    (19, 7, 3862039, 2),
    (20, 7, 3862040, 3),
    (21, 8, 3952501, 1),
    (22, 8, 3952566, 2),
    (23, 8, 3952568, 3),
    (24, 9, 2552622, 1),
    (25, 9, 3829060, 2),
    (26, 9, 3838425, 3),
    (27, 10, 3354267, 1),
    (28, 10, 3806772, 2),
    (29, 10, 3806779, 3),
    (30, 11, 3502745, 1),
    (31, 11, 3502747, 2),
    (32, 11, 3502795, 3),
    (33, 12, 3951435, 1),
    (34, 12, 3954903, 2),
    (35, 12, 3954968, 3),
    (36, 13, 3725257, 1),
    (37, 13, 3725258, 2),
    (38, 13, 3745924, 3),
    (39, 14, 2234834, 1),
    (40, 14, 2801901, 2),
    (41, 14, 2905764, 3);
