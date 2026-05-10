# API 명세서 스냅샷

## 1. 스냅샷 정보

- Snapshot date: 2026-05-10
- Source: 개인 Notion 워크스페이스의 API 명세서
- 사용자 확인: 개인 Notion 문서는 팀스페이스 API 명세서와 동기화된 상태다.
- 실제 SSoT: 팀스페이스 Notion API 명세서

이 문서는 AI와 로컬 개발자가 외부 권한 없이 API 계약을 확인하기 위한 dated snapshot이다.
팀스페이스 API 명세서와 충돌하면 팀스페이스 API 명세서를 우선한다.

## 2. 기본 정보

- Base URL: `https://<base-url>/api`
- 하위 API URL 표기: Base URL 이후 path(`/v1/...`)만 작성한다.
- 로컬 구현 기준 전체 path 예시: `/api/v1/home`
- API Version: `v0.1.0`
- Content-Type: `application/json`
- Character Encoding: `UTF-8`
- 인증/인가는 구현하지 않는다.
- 별도 `Authorization` 헤더를 사용하지 않는다.

## 3. 공통 응답 규격

모든 API는 공통 응답 형식을 사용한다.

### 성공 응답

```json
{
  "code": "GLB-S001",
  "success": true,
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

현재 프로젝트 범위의 성공 응답은 기본적으로 `GLB-S001`을 사용한다.
도메인별 success code는 현재 사용하지 않지만, 추후 특정 API 성공 상태를 클라이언트가 구분해야 하는 요구가 생기면 `{DOMAIN}-S###` 형식으로 확장할 수 있다.

### 에러 응답

```json
{
  "code": "GLB-E001",
  "success": false,
  "message": "잘못된 요청입니다."
}
```

응답 데이터가 없는 경우 `data` 필드는 응답에서 생략될 수 있다.
목록 응답의 데이터가 없는 경우 `null`이 아닌 빈 배열 `[]`로 응답한다.

## 4. 공통 코드

### 성공 코드

| Code | HTTP Status | Message | Description |
| --- | --- | --- | --- |
| `GLB-S001` | 200 | 요청이 성공했습니다. | 공통 성공 응답 |
| `GLB-S002` | 201 | 리소스가 생성되었습니다. | 리소스 생성 성공. 현재 API 범위에서는 기본 사용하지 않음 |
| `GLB-S003` | 204 | 요청이 성공적으로 처리되었습니다. | 응답 본문 없는 성공. 현재 API 범위에서는 기본 사용하지 않음 |

### 공통 에러 코드

| Code | HTTP Status | Message | Description |
| --- | --- | --- | --- |
| `GLB-E001` | 400 | 잘못된 요청입니다. | Validation 실패, JSON 파싱 실패, 잘못된 query/path parameter |
| `GLB-E002` | 401 | 인증이 필요합니다. | 현재 프로젝트에서는 기본적으로 사용하지 않음 |
| `GLB-E003` | 403 | 접근 권한이 없습니다. | 인증/인가 기반으로는 기본적으로 사용하지 않음 |
| `GLB-E004` | 404 | 요청한 리소스를 찾을 수 없습니다. | 존재하지 않는 리소스 또는 경로 요청 |
| `GLB-E005` | 500 | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

### 도메인 에러 코드

| Code | HTTP Status | Message | Description |
| --- | --- | --- | --- |
| `CATEGORY-E001` | 404 | 카테고리를 찾을 수 없습니다. | 카테고리 리소스가 존재하지 않음 |
| `PRODUCT-E001` | 404 | 상품을 찾을 수 없습니다. | 상품 리소스가 존재하지 않음 |
| `PRODUCT-E002` | 403 | 로그인이 필요한 기능입니다. | 비로그인 상태에서 좋아요 토글 요청 |

## 5. 공통 파라미터 정책

### 날짜 형식

| Type | Format | Example |
| --- | --- | --- |
| LocalDate | `YYYY-MM-DD` | `2026-04-29` |
| LocalDateTime | `YYYY-MM-DDTHH:mm:ss` | `2026-04-29T10:30:00` |

### viewerType

| Value | Meaning |
| --- | --- |
| `guest` | 비로그인 사용자 |
| `user` | 로그인한 테스트 사용자 |

- 미전달 시 `guest`로 처리한다.
- 허용 값은 `user`, `guest`다.
- 허용 값 외 입력 시 `400 GLB-E001`을 반환한다.
- 조회 API에서 `viewerType=guest`이면 상품의 `isLiked`는 항상 `false`다.
- `viewerType=user`는 seed 데이터에 등록된 단일 테스트 사용자 `id=1`을 의미한다.

### cursor

- cursor는 opaque string이다.
- 클라이언트는 cursor를 해석하지 않고 응답받은 값을 그대로 다음 요청에 전달한다.
- 서버 내부적으로 `displayOrder + id` 기준 값을 Base64 인코딩해 생성한다.
- 잘못된 cursor는 `400 GLB-E001`을 반환한다.
- 다음 데이터가 없으면 `pageInfo.hasNext=false`, `pageInfo.nextCursor=null`로 응답한다.

## 6. API 목록

| API | Method | Path |
| --- | --- | --- |
| 홈 캐러셀 조회 | GET | `/v1/home/carousels` |
| 홈 메인 조회 | GET | `/v1/home` |
| 쇼케이스 피드 조회 | GET | `/v1/showcases` |
| 상품 좋아요 토글 | POST | `/v1/products/{productId}/like` |
| 카테고리 메가 메뉴 조회 | GET | `/v1/nav` |
| 푸터 공지 조회 | GET | `/v1/notices` |

## 7. 홈 캐러셀 조회

### `GET /v1/home/carousels`

홈 상단 캐러셀 영역에 노출할 캐러셀 이미지 목록을 조회한다.
홈 메인 조회 API와 분리되어 있으며, 기획에서 제공한 더미데이터 순서대로 내려준다.

### Request

별도 header, query string, request body를 사용하지 않는다.

### Response Data

```json
{
  "carousels": [
    {
      "carouselId": 1,
      "imageUrl": "https://s3.example.com/home/banner-1.png",
      "altText": "여름 시즌 기획전 배너"
    }
  ]
}
```

| Field | Type | Description |
| --- | --- | --- |
| `carousels` | Array | 홈 상단 캐러셀 이미지 목록 |
| `carousels[].carouselId` | Long | 캐러셀 ID |
| `carousels[].imageUrl` | String | 캐러셀 이미지 URL |
| `carousels[].altText` | String | 이미지 대체 텍스트 |

### Error

| HTTP Status | Code | Description |
| --- | --- | --- |
| 500 | `GLB-E005` | 서버 내부 오류 |

### Notes

- `/v1/home` 응답에는 캐러셀을 포함하지 않는다.
- 노출 기간 정보는 응답에 포함하지 않는다.
- 응답 배열 순서는 서버가 결정한 노출 순서다.
- 캐러셀 클릭 이동 URL은 현재 응답에 포함하지 않는다.

## 8. 홈 메인 조회

### `GET /v1/home`

홈 화면에서 필요한 숏컷과 메인 셀렉션 섹션 데이터를 조회한다.
홈 캐러셀은 별도 API에서 조회하며, 메인 피드 섹션은 cursor 기반 무한스크롤로 조회한다.

### Query String

| Name | Type | Required | Default | Description |
| --- | --- | --- | --- | --- |
| `viewerType` | String | No | `guest` | `user`, `guest`만 허용 |
| `cursor` | String | No | 없음 | 다음 메인 피드 섹션 조회 기준 opaque cursor |
| `size` | Integer | No | `5` | 한 번에 조회할 메인 피드 섹션 개수. 최소 `1`, 최대 `20` |

### Response Data

```json
{
  "shortcuts": [
    {
      "shortcutId": 1,
      "name": "BEST",
      "imageUrl": "https://s3.example.com/home/shortcut-best.png",
      "categoryId": 1
    }
  ],
  "sections": [
    {
      "sectionId": 1,
      "title": "매일 스며드는 스타일",
      "description": "일상에 자연스럽게 스며드는 옷",
      "heroImageUrl": "https://s3.example.com/home/section-1-main.png",
      "selections": [
        {
          "selectionId": 11,
          "imageUrl": "https://s3.example.com/home/selection-1.png",
          "title": "왕수빈이 고른 노티아",
          "description": "편안함 속에 깃든 노티아의 스타일을 만나요.",
          "products": [
            {
              "productId": 1001,
              "imageUrl": "https://s3.example.com/products/1001.png",
              "brandName": "노티아",
              "name": "COTTON TWO TUCK PANTS - BLACK",
              "saleRate": 29,
              "price": 83250,
              "tags": ["무료배송", "조건부 무료배송", "단독"],
              "likeCount": 35000,
              "isLiked": false
            }
          ]
        }
      ]
    }
  ],
  "pageInfo": {
    "nextCursor": "eyJkaXNwbGF5T3JkZXIiOjEyLCJzZWN0aW9uSWQiOjEyfQ==",
    "hasNext": true,
    "size": 5
  }
}
```

| Field | Type | Description |
| --- | --- | --- |
| `shortcuts` | Array | 카테고리 숏컷 목록 |
| `shortcuts[].shortcutId` | Long | 숏컷 ID |
| `shortcuts[].name` | String | 숏컷 이름 |
| `shortcuts[].imageUrl` | String | 숏컷 이미지 URL |
| `shortcuts[].categoryId` | Long | 연결된 카테고리 ID |
| `sections` | Array | 현재 페이지에 해당하는 좌측 대표 이미지와 우측 셀렉션 묶음 |
| `sections[].sectionId` | Long | 홈 섹션 ID |
| `sections[].title` | String | 홈 섹션 제목 |
| `sections[].description` | String | 홈 섹션 설명 |
| `sections[].heroImageUrl` | String | 홈 섹션 대표 이미지 URL |
| `sections[].selections` | Array | 섹션 우측에 노출되는 셀렉션 카드 목록 |
| `sections[].selections[].selectionId` | Long | 셀렉션 ID |
| `sections[].selections[].imageUrl` | String | 셀렉션 이미지 URL |
| `sections[].selections[].title` | String | 셀렉션 제목 |
| `sections[].selections[].description` | String | 셀렉션 설명 |
| `sections[].selections[].products` | Array | 셀렉션 하위 상품 목록 |
| `products[].productId` | Long | 상품 ID |
| `products[].imageUrl` | String | 상품 이미지 URL |
| `products[].brandName` | String | 브랜드명 |
| `products[].name` | String | 상품명 |
| `products[].price` | Integer | 최종 판매가 |
| `products[].saleRate` | Integer | 할인율. 할인 없음이면 `0` |
| `products[].tags` | Array<String> | 상품 카드 태그 목록. seed 기반 자유 문자열 배열 |
| `products[].likeCount` | Integer | 상품 좋아요 수 |
| `products[].isLiked` | Boolean | 상품 좋아요 여부 |
| `pageInfo.nextCursor` | String \| null | 다음 페이지 요청 시 전달할 cursor |
| `pageInfo.hasNext` | Boolean | 다음 페이지 존재 여부 |
| `pageInfo.size` | Integer | 해당 요청에 실제 적용된 page size |

### Error

| HTTP Status | Code | Description |
| --- | --- | --- |
| 400 | `GLB-E001` | 잘못된 `viewerType`, `cursor`, `size` 요청 |
| 500 | `GLB-E005` | 서버 내부 오류 |

### Notes

- 홈 캐러셀은 별도 API에서 조회하며, 본 API 응답에는 포함하지 않는다.
- 숏컷은 nav-bar 카테고리와 중복될 수 있으나, 홈 화면 구성 데이터로 별도 관리한다.
- `viewerType=guest`인 경우 상품의 `isLiked`는 항상 `false`다.
- `viewerType=user`는 seed 데이터에 등록된 단일 테스트 사용자 `id=1`을 의미하며, 해당 사용자 기준으로 `isLiked`를 계산한다.
- 상품의 `price`는 최종 판매가다.
- 상품의 `saleRate`는 할인율이며, 할인 정보가 없으면 `0`으로 응답한다.
- 상품의 `tags`는 서버가 의미를 해석하지 않는 seed 기반 자유 문자열 배열이다.
- 무한스크롤 요청 중에는 최초 요청과 동일한 `viewerType`을 유지한다.
- 로그인/비로그인 상태에 따라 main view 콘텐츠의 상품 노출 순서가 달라질 수 있다. 응답 형식은 동일하다.

## 9. 쇼케이스 피드 조회

### `GET /v1/showcases`

Showcase 화면의 상단 피처드 카드와 테마별 쇼케이스 피드 리스트를 조회한다.
쇼케이스 피드는 cursor 기반 무한스크롤로 조회한다.

### Query String

| Name | Type | Required | Default | Description |
| --- | --- | --- | --- | --- |
| `theme` | String | No | 전체 | 특정 테마만 조회할 때 사용 |
| `cursor` | String | No | 없음 | 다음 쇼케이스 피드 조회 기준 opaque cursor |
| `size` | Integer | No | `9` | 한 번에 조회할 피드 개수. 최소 `1`, 최대 `20` |

### Response Data

```json
{
  "featured": [
    {
      "showcaseId": 1,
      "title": "우리만의 상상 바캉스 로토토베베",
      "description": "아이들의 행복한 순간을 담은 로토토베베의 이야기",
      "imageUrl": "https://s3.example.com/showcases/featured-1.png",
      "startDate": "2026-04-29",
      "endDate": "2026-05-12"
    }
  ],
  "sections": [
    {
      "sectionId": 1,
      "theme": "LIFESTYLE",
      "title": "당신의 취향에 맞춘 잡화 셀렉션",
      "feeds": [
        {
          "showcaseId": 11,
          "title": "전통과 현대가 만나는 순간 루트파인더",
          "description": "유연한 실루엣의 여름 컬렉션을 만나보세요.",
          "imageUrl": "https://s3.example.com/showcases/11.png",
          "startDate": "2026-04-29",
          "endDate": "2026-05-12"
        }
      ]
    }
  ],
  "pageInfo": {
    "nextCursor": "eyJkaXNwbGF5T3JkZXIiOjExLCJzaG93Y2FzZUlkIjoxMX0=",
    "hasNext": true,
    "size": 9
  }
}
```

| Field | Type | Description |
| --- | --- | --- |
| `featured` | Array | 상단 2열 피처드 쇼케이스 목록 |
| `sections` | Array | 테마별 쇼케이스 섹션 목록 |
| `sections[].theme` | String | 섹션 테마. 예: `LIFESTYLE` |
| `sections[].feeds` | Array | 현재 페이지에 해당하는 섹션 하위 쇼케이스 카드 목록 |
| `startDate`, `endDate` | LocalDate | 쇼케이스 노출 또는 이벤트 기간 원본 날짜 |
| `pageInfo.nextCursor` | String \| null | 다음 페이지 요청 시 전달할 cursor |
| `pageInfo.hasNext` | Boolean | 다음 페이지 존재 여부 |
| `pageInfo.size` | Integer | 해당 요청에 실제 적용된 page size |

### Error

| HTTP Status | Code | Description |
| --- | --- | --- |
| 400 | `GLB-E001` | 지원하지 않는 theme 값 |
| 400 | `GLB-E001` | 잘못된 cursor |
| 400 | `GLB-E001` | size 범위 위반 |
| 500 | `GLB-E005` | 서버 내부 오류 |

### Notes

- `featured`는 첫 요청, 즉 `cursor`가 없는 요청에서만 응답한다.
- `cursor`가 있는 후속 요청에서는 `featured=[]`로 응답한다.
- `featured`와 일반 `feeds`에 같은 쇼케이스가 중복 포함될 수 있다.
- `displayOrder`는 서버 내부 정렬 및 seed 관리용 필드이며 API 응답에는 포함하지 않는다.
- cursor는 서버 내부적으로 `displayOrder + showcaseId` 기준 값을 Base64 인코딩해 생성한다.
- `pageInfo.hasNext=false`이면 더 이상 요청하지 않는다.
- `Event`, `Lookbook` 탭처럼 쇼케이스 내부 필터가 실제 동작한다면 `type` query param 추가를 검토한다.

## 10. 상품 좋아요 토글

### `POST /v1/products/{productId}/like`

상품 카드의 좋아요 버튼을 눌렀을 때 좋아요 상태를 토글한다.

### Path Variable

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| `productId` | Long | Yes | 좋아요 토글 대상 상품 ID |

### Query String

| Name | Type | Required | Default | Description |
| --- | --- | --- | --- | --- |
| `viewerType` | String | No | `guest` | `user`인 경우 단일 seed 사용자 `id=1` 기준으로 토글 수행. `guest`인 경우 `403 PRODUCT-E002` |

### Response Data

```json
{
  "productId": 1001,
  "isLiked": true,
  "likeCount": 35001
}
```

| Field | Type | Description |
| --- | --- | --- |
| `productId` | Long | 상품 ID |
| `isLiked` | Boolean | 토글 후 좋아요 여부 |
| `likeCount` | Integer | 토글 후 좋아요 수 |

### Error

| HTTP Status | Code | Description |
| --- | --- | --- |
| 400 | `GLB-E001` | 잘못된 `viewerType` 요청 |
| 403 | `PRODUCT-E002` | 로그인 필요한 기능. `viewerType=guest` 또는 미전달인 경우 좋아요 토글 불가 |
| 404 | `PRODUCT-E001` | 상품을 찾을 수 없음 |
| 500 | `GLB-E005` | 서버 내부 오류 |

### Notes

- 별도 인증/인가는 사용하지 않지만, 좋아요 토글은 `viewerType=user`인 경우에만 허용한다.
- `viewerType=user`는 seed 데이터에 등록된 단일 테스트 사용자 `id=1`을 의미한다.
- `viewerType=guest`이거나 `viewerType`을 전달하지 않은 경우 `403 PRODUCT-E002`를 반환하며 좋아요 상태를 변경하지 않는다.
- 홈 메인 조회에서 `viewerType=guest`인 상품의 `isLiked`는 항상 `false`다.

## 11. 카테고리 메가 메뉴 조회

### `GET /v1/nav`

Shopping/Home 상단 카테고리 메가 메뉴에서 사용할 대분류, 중분류, 세부 카테고리 전체 목록을 한 번에 조회한다.

### Request

별도 header, query string, request body를 사용하지 않는다.

### Response Data

```json
{
  "categories": [
    {
      "topCategoryId": 1,
      "name": "BEST",
      "middleCategories": [
        {
          "middleCategoryId": 101,
          "name": "의류",
          "subCategories": [
            {
              "subCategoryId": 1001,
              "name": "단독"
            }
          ]
        }
      ]
    }
  ]
}
```

| Field | Type | Description |
| --- | --- | --- |
| `categories` | Array | 상단 대분류 목록 |
| `categories[].topCategoryId` | Long | 대분류 ID |
| `categories[].name` | String | 대분류 이름 |
| `categories[].middleCategories` | Array | 대분류 하위 중분류 목록 |
| `categories[].middleCategories[].middleCategoryId` | Long | 중분류 ID |
| `categories[].middleCategories[].name` | String | 중분류 이름 |
| `categories[].middleCategories[].subCategories` | Array | 중분류 하위 세부 카테고리 목록 |
| `categories[].middleCategories[].subCategories[].subCategoryId` | Long | 세부 카테고리 ID |
| `categories[].middleCategories[].subCategories[].name` | String | 세부 카테고리 이름 |

### Error

| HTTP Status | Code | Description |
| --- | --- | --- |
| 500 | `GLB-E005` | 서버 내부 오류 |

### Notes

- 카테고리 메가 메뉴는 서버 API에서 관리한다.
- 첫 진입 시 한 번에 전체 세부 카테고리를 내려받는 구조다.
- 응답 배열 순서는 서버가 결정한 노출 순서다.
- `Shopping`, `Special-Order`, `Showcase`, `PT`, `29Magazine` 같은 1차 글로벌 메뉴는 본 API 응답 범위에서 제외한다.
- 홈 숏컷과 메가 메뉴는 같은 Category 데이터를 참조할 수 있으며, API 응답에 카테고리 정보가 중복 등장하는 것은 의도된 설계다.
- 대분류 예시: `BEST`, `WOMEN`, `MEN`, `INTERIOR`, `KITCHEN`, `ELECTRONICS`, `DIGITAL`, `BEAUTY`, `FOOD`, `LEISURE`, `KIDS`, `CULTURE`, `EARTH`

## 12. 푸터 공지 조회

### `GET /v1/notices`

푸터 영역에 노출되는 NOTICE 목록을 조회한다.

### Request

별도 header, query string, request body를 사용하지 않는다.

### Response Data

```json
{
  "notices": [
    {
      "noticeId": 1,
      "title": "공지 제목"
    }
  ]
}
```

| Field | Type | Description |
| --- | --- | --- |
| `notices` | Array | 푸터에 노출할 공지 목록 |
| `notices[].noticeId` | Long | 공지 ID |
| `notices[].title` | String | 공지 제목 |

### Error

| HTTP Status | Code | Description |
| --- | --- | --- |
| 500 | `GLB-E005` | 서버 내부 오류 |

### Notes

- 본 API는 푸터 NOTICE 영역에 노출되는 공지 목록만 담당한다.
- seed 데이터의 index를 공지 순서로 사용하며, index 기준 가장 최근 5개 공지만 반환한다.
- 공지 클릭 이동은 제공하지 않으므로 `linkUrl`은 응답에 포함하지 않는다.
- 고객센터, SNS, ABOUT US/MY ORDER/MY ACCOUNT/HELP 링크, 사업자 정보는 본 API 범위에서 제외한다.
- 푸터는 홈과 쇼케이스 양쪽에서 공통으로 재사용될 수 있어 `공통` 탭으로 분류한다.
