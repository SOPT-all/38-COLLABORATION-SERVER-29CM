# ERD 설계서

## 1. 설계 기준

- API 계약의 SSoT는 Notion API 명세서다.
- 제품 및 서버 요구사항은 `docs/spec/REQUIREMENTS.md`를 따른다.
- 더미데이터는 DB seed 방식으로 관리하며, Spring Boot `data.sql` 사용을 기본으로 한다.
- ERD 원본은 `docs/erd/29cm.dbml`이며 dbdiagram.io에서 시각화한다.

## 2. 공통 가정

- `created_at`은 현재 `products`, `product_likes`에만 둔다.
- `updated_at`은 실제 변경이 발생하는 테이블에만 둔다.
- 현재 변경이 발생하는 주요 데이터는 `products.like_count`이므로 `products`에만 `updated_at`을 둔다.
- 화면 노출 순서는 `display_order`로 관리한다.
- 같은 `display_order`가 있을 수 있으므로 조회 시 `id`를 보조 정렬 기준으로 사용한다.
- 별도 관리자 기능이 없으므로 `is_active` 플래그는 두지 않는다.
- seed에 포함된 데이터는 API 노출 대상 데이터로 간주한다.
- cursor pagination은 `display_order + id` 기준 값을 Base64 인코딩해 사용한다.
- 이미지 파일 업로드 API는 제공하지 않으며, DB에는 최종 이미지 URL 문자열만 저장한다.

## 3. 주요 도메인 결정

### User

- 실제 로그인/회원가입은 구현하지 않는다.
- `viewerType=user`는 seed 데이터의 단일 테스트 사용자 `users.id = 1`을 의미한다.
- 좋아요 토글은 항상 `user_id = 1` 기준으로 처리한다.

### Category

- `categories`는 top, middle, sub 카테고리를 하나의 self-reference 테이블로 관리한다.
- `categories.depth`는 `1=top`, `2=middle`, `3=sub`을 의미한다.
- 홈 숏컷은 같은 `categories` 데이터를 참조하되, 홈 전용 이미지와 노출 순서는 `home_shortcuts`에서 별도 관리한다.

### Home

- 홈 캐러셀은 `/home` 응답과 분리된 별도 API 대상이다.
- 홈 섹션, 셀렉션, 상품은 `home_sections -> home_selections -> selection_products -> products` 구조로 연결한다.
- 같은 상품은 여러 셀렉션에 중복 노출될 수 있다.

### Product

- `products.price`는 최종 판매가다.
- `products.sale_rate`는 할인율이며, 할인 정보가 없으면 `0`이다.
- 현재 범위에서는 `original_price`를 두지 않는다.
- 상품 태그는 `product_tags` 별도 테이블로 관리한다.
- 상품 태그는 seed 기반 자유 문자열이며, 서버가 의미를 해석하지 않는다.
- `products.like_count`는 좋아요 토글에 따라 실제 증감한다.
- `products`는 `created_at`, `updated_at`을 둔다.

### Product Like

- `product_likes`는 현재 좋아요 상태를 나타낸다.
- 좋아요 토글은 row 생성/삭제 방식으로 처리한다.
- `user_id + product_id`는 unique다.
- row 생성 시점만 의미가 있으므로 `created_at`만 둔다.

### ShowCase

- `showcase_sections`는 테마별 섹션을 관리한다.
- `showcase_sections.description`은 현재 API 응답에서 제외되므로 ERD v1에는 두지 않는다.
- `showcases`는 featured와 일반 feed를 같은 테이블에서 관리한다.
- `is_featured=true`인 데이터가 있으면 해당 feed를 featured로 사용한다.
- `is_featured` 지정이 없으면 `display_order` 기준 마지막 2개를 featured로 사용한다.
- featured와 일반 feed의 중복 노출은 허용한다.
- `display_order` 값이 클수록 더 최신 또는 뒤쪽 index로 본다.

### Notice

- seed에 포함된 모든 공지는 노출 가능한 공지로 간주한다.
- 별도 활성/비활성 플래그는 두지 않는다.
- `display_order`를 seed index 및 공지 순서로 사용한다.
- 값이 큰 `display_order`를 더 최신 공지로 보고, 푸터 API는 최신 5개를 반환한다.

## 4. 테이블 목록

| 테이블 | 설명 |
| --- | --- |
| `users` | 단일 테스트 사용자 |
| `categories` | 카테고리 계층 |
| `home_shortcuts` | 홈 카테고리 숏컷 |
| `home_carousels` | 홈 상단 캐러셀 |
| `home_sections` | 홈 메인 섹션 |
| `home_selections` | 홈 섹션 내부 셀렉션 |
| `products` | 상품 카드 |
| `product_tags` | 상품 태그 |
| `selection_products` | 셀렉션과 상품의 연결 |
| `product_likes` | 단일 테스트 사용자의 상품 좋아요 상태 |
| `showcase_sections` | 쇼케이스 테마 섹션 |
| `showcases` | 쇼케이스 카드 |
| `notices` | 푸터 공지 |

## 5. 주요 제약

- `categories.parent_id`는 `categories.id`를 참조한다.
- `home_shortcuts.category_id`는 `categories.id`를 참조한다.
- `selection_products.home_selection_id + selection_products.product_id`는 unique다.
- `product_likes.user_id + product_likes.product_id`는 unique다.
- `showcase_sections.theme`은 unique다.

## 6. 시각화 방법

1. `docs/erd/29cm.dbml` 내용을 복사한다.
2. dbdiagram.io에 붙여넣는다.
3. 렌더링된 ERD를 기준으로 팀 리뷰를 진행한다.
