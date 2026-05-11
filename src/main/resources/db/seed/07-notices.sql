INSERT INTO notices (id, title, display_order)
VALUES (1, '[공지] 개인정보 처리방침 개정 안내', 6),
       (2, '[공지] 29CM 서비스 이용약관 개정 안내', 5),
       (3, '[공지] 고객센터 운영 시간 변경 안내', 4),
       (4, '[공지] 배송 지연 지역 안내', 3),
       (5, '[공지] 설 연휴 배송 및 고객센터 운영 안내', 2),
       (6, '[공지] 앱 업데이트 및 일부 기능 개선 안내', 1);

ALTER TABLE notices AUTO_INCREMENT = 7;
