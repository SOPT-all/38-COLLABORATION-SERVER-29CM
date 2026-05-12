-- 임시 더미데이터. 기획 전달 JSON 수신 후 교체 예정.
-- 단일 테스트 사용자(user_id=1) 기준 좋아요 상태.
INSERT INTO product_likes (id, user_id, product_id, created_at)
VALUES (1, 1, 1001, NOW()),
       (2, 1, 1003, NOW()),
       (3, 1, 1006, NOW()),
       (4, 1, 1010, NOW());
