# 29CM Collaboration Server 요구사항 명세서

## 1. 문서 목적

이 문서는 29CM 데스크톱 웹 리디자인 세미나 프로젝트의 서버 구현 범위와 기능 요구사항을 정리한다.

본 문서는 이후 다음 산출물의 기준 문서로 사용한다.

- ERD 및 도메인 모델 설계
- API 구현 설계의 요구사항 입력
- DB seed 데이터 설계
- 더미데이터 전달 양식
- FE-BE 연동 시 API 명세서와 함께 참고할 요구사항 기준

문서 우선순위:

- API 경로, request, response, HTTP status, error code의 최종 계약은 별도 API 명세서를 SSoT로 따른다.
- 본 문서는 API 명세서가 왜 그런 형태를 가져야 하는지에 대한 제품/서버 요구사항과 설계 의도를 정리한다.
- 본 문서와 API 명세서가 충돌하면 API 명세서를 우선하고, 본 문서를 API 명세서에 맞춰 갱신한다.
- 본 문서에서 API 명세서 변경이 필요한 요구사항을 발견하면 API 명세서에 먼저 반영한 뒤 구현한다.

## 2. 프로젝트 개요

본 프로젝트는 29CM 데스크톱 웹의 Main 화면과 ShowCase 화면을 리디자인하는 세미나 프로젝트다.

서버는 웹 클라이언트가 화면을 구성하는 데 필요한 데이터를 REST API로 제공한다. 데이터는 실제 운영 데이터가 아니라 프로젝트 구현 범위에 맞춘 더미데이터를 사용하며, 서버에서는 해당 더미데이터를 DB seed 방식으로 적재해 조회한다.

## 3. 구현 범위

### 3.1 포함 범위

| 화면 또는 기능 | 포함 여부 | 설명 |
| --- | --- | --- |
| Main View | 포함 | 홈 화면 |
| ShowCase View | 포함 | 쇼케이스 목록 화면 |
| Header | 포함 | Main, ShowCase에서 공통 사용 |
| Footer Notice | 포함 | 푸터 공지 목록 |
| Product Like | 포함 | Main 화면 상품 카드 좋아요 |
| Category Mega Menu | 포함 | 상단 카테고리 메가 메뉴 |

### 3.2 제외 범위

| 화면 또는 기능 | 제외 사유 |
| --- | --- |
| Category Product List | 이번 구현 범위에 별도 화면이 없음 |
| ShowCase Detail | 카드 클릭 시 대체 화면 이동으로 처리 가능 |
| Brand Detail | 선택 범위이며, 서버 상세 API는 현재 범위에서 제외 |
| 실제 로그인/회원가입 | 인증/인가를 구현하지 않음 |
| 결제/장바구니/주문 | 이번 화면 범위와 무관 |
| 관리자 기능 | 더미데이터는 DB seed로 관리 |

## 4. 공통 정책

### 4.1 API 기본 정책

- 본 문서의 API 관련 표와 필드는 요구사항 관점의 요약이다.
- API 계약의 최종 SSoT는 별도 API 명세서다.
- Base path는 `/api/v1`을 사용한다.
- 모든 API는 JSON 형식으로 응답한다.
- 인증/인가는 구현하지 않는다.
- 별도의 `Authorization` 헤더를 사용하지 않는다.
- 모든 API 응답은 공통 응답 형식을 사용한다.

### 4.2 공통 응답 형식

성공 응답은 다음 형식을 따른다.

```json
{
  "code": "GLB-S001",
  "success": true,
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

정책:

- 현재 범위의 성공 응답 코드는 모든 API에서 `GLB-S001`을 사용한다.
- 도메인별 success code는 현재 사용하지 않는다.
- 추후 특정 API 성공 상태를 클라이언트가 구분해야 하는 요구가 생기면 `{DOMAIN}-S###` 형식의 success code를 추가할 수 있다.

실패 응답은 다음 형식을 따른다.

```json
{
  "code": "GLB-E001",
  "success": false,
  "message": "잘못된 요청입니다.",
  "data": null
}
```

### 4.3 사용자 상태

본 프로젝트는 실제 로그인 기능을 구현하지 않는다.

다만 Main 화면의 상품 좋아요 상태와 좋아요 토글 기능을 표현하기 위해 `viewerType` query parameter를 사용한다.

| 값 | 의미 |
| --- | --- |
| `guest` | 비로그인 사용자 |
| `user` | 로그인한 테스트 사용자 |

정책:

- `viewerType`이 없으면 기본값은 `guest`다.
- 허용 값은 `guest`, `user` 두 가지다.
- 허용되지 않은 값은 `400 GLB-E001`로 응답한다.
- `viewerType=user`는 단일 테스트 사용자를 의미한다.
- 단일 테스트 사용자를 저장하기 위한 별도 User 테이블을 둔다.
- seed 데이터에 등록된 단일 테스트 사용자는 `id=1`로 고정한다.
- `viewerType=user` 요청은 별도 인증 없이 `user_id=1` 기준으로 처리한다.
- 조회 API에서 `viewerType`이 없으면 `guest`로 정상 처리한다.
- 좋아요 토글 API에서 `viewerType`이 없으면 `guest`와 동일하게 보고 `403 PRODUCT-E002`로 응답한다.
- 실제 세션, 토큰, 권한 관리는 구현하지 않는다.

### 4.4 이미지 정책

- 서버는 이미지 URL 문자열을 응답한다.
- 더미데이터 seed에는 S3 객체 키를 저장한다.
- API 응답 조립 시 S3 객체 키를 presigned URL로 변환한다.
- 기획 전달 JSON에는 이미지 식별자 또는 파일명을 포함할 수 있으며, seed 적재 시 S3 객체 키로 정리한다.
- 서버는 프로젝트 범위 내에서 이미지 파일 업로드 API를 제공하지 않는다.
- 서버는 이미지 최적화, 리사이징, CDN 처리를 담당하지 않는다.
- 이미지 파일의 S3 업로드 담당자, bucket/path, 파일명 규칙은 기획 측 응답 후 확정한다.

### 4.5 정렬 정책

- 화면 노출 순서는 서버가 결정한다.
- 클라이언트는 서버 응답 배열 순서대로 렌더링한다.
- 노출 순서가 필요한 데이터에는 `displayOrder` 성격의 값을 둔다.
- 같은 `displayOrder`가 존재할 가능성에 대비해 ID 오름차순을 보조 정렬 기준으로 사용할 수 있다.

### 4.6 페이지네이션 정책

Main 피드와 ShowCase 피드는 cursor 기반 무한스크롤을 사용한다.

정책:

- 첫 요청에는 `cursor`를 전달하지 않는다.
- 다음 페이지 요청에는 응답의 `pageInfo.nextCursor`를 그대로 전달한다.
- cursor는 opaque string으로 취급하며 클라이언트가 해석하지 않는다.
- cursor는 `displayOrder + id` 값을 담은 Base64 인코딩 문자열을 사용한다.
- 잘못된 cursor는 `400 GLB-E001`로 응답한다.
- 다음 데이터가 없으면 `pageInfo.hasNext=false`, `pageInfo.nextCursor=null`로 응답한다.
- cursor pagination을 사용하는 API는 `pageInfo.nextCursor`, `pageInfo.hasNext`, `pageInfo.size`를 응답한다.
- `pageInfo.size`는 해당 요청에 실제 적용된 page size를 의미한다.

## 5. 데이터 관리 정책

### 5.1 DB seed 방식

본 프로젝트의 더미데이터는 DB seed 방식으로 관리한다.

의미:

- 더미데이터는 애플리케이션 실행 전에 DB 테이블에 적재된다.
- 서버는 실제 API 구현과 동일하게 Repository/JPA를 통해 데이터를 조회한다.
- API 응답은 DB에 seed된 데이터를 기반으로 생성한다.
- seed 적재 방식은 Spring Boot의 `data.sql` 사용을 기본으로 한다.

기대 효과:

- ERD와 실제 구현이 연결된다.
- JPA Repository 기반 조회 흐름을 검증할 수 있다.
- 더미데이터 변경 시 seed 데이터만 수정하면 된다.

### 5.2 Seed 대상 데이터

초기 seed 대상 후보는 다음과 같다.

| 데이터 | 설명 |
| --- | --- |
| Category | 카테고리 메가 메뉴 및 홈 숏컷에 사용 |
| Home Carousel | 홈 상단 배너 캐러셀 |
| Home Section | 홈 메인 피드의 큰 큐레이션 섹션 |
| Home Selection | 섹션 내부 셀렉션 카드 |
| Product | 상품 카드 데이터 |
| Product Tag | 상품 카드에 노출할 태그 |
| User | 단일 테스트 사용자 |
| Product Like | 단일 테스트 사용자의 상품 좋아요 상태 |
| ShowCase | 쇼케이스 카드 데이터 |
| ShowCase Section | 쇼케이스 화면의 테마별 섹션 |
| Notice | 푸터 공지 |

## 6. 기능 요구사항

### 6.1 홈 캐러셀 조회

### 목적

Main 화면 상단 캐러셀 영역에 노출할 배너 목록을 조회한다.

### API

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/home/carousels` |
| Auth | 미사용 |

### 입력

별도 header, query string, request body를 사용하지 않는다.

### 처리 규칙

- seed에 포함된 캐러셀은 노출 대상 캐러셀로 간주한다.
- 응답 배열은 노출 순서대로 정렬한다.
- 캐러셀 클릭 이동 URL은 현재 응답에 포함하지 않는다.
- 홈 메인 조회 API 응답에는 캐러셀 데이터를 포함하지 않는다.

### 응답 데이터

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `carousels` | Array | 홈 상단 캐러셀 목록 |
| `carousels[].carouselId` | Long | 캐러셀 ID |
| `carousels[].imageUrl` | String | 이미지 URL |
| `carousels[].altText` | String | 이미지 대체 텍스트 |

### 예외

| 상황 | HTTP Status | Error Code |
| --- | --- | --- |
| 서버 내부 오류 | 500 | `GLB-E005` |

### 빈 상태

노출 가능한 캐러셀이 없으면 `carousels=[]`를 반환한다.

### 6.2 홈 메인 조회

### 목적

Main 화면에서 필요한 카테고리 숏컷과 메인 큐레이션 피드 섹션을 조회한다.

### API

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/home` |
| Auth | 미사용 |

### 입력

| Query | 타입 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- | --- |
| `viewerType` | String | No | `guest` | `user`, `guest`만 허용 |
| `cursor` | String | No | 없음 | 다음 페이지 조회 기준 cursor |
| `size` | Integer | No | `5` | 최소 `1`, 최대 `20` |

### 처리 규칙

- 홈 캐러셀 데이터는 포함하지 않는다.
- 카테고리 숏컷은 홈 화면 전용 노출 순서대로 반환한다.
- `viewerType=guest`인 경우 섹션은 기존 노출 순서대로 반환한다.
- `viewerType=guest`인 경우 셀렉션은 기존 노출 순서대로 반환한다.
- `viewerType=guest`인 경우 상품은 좋아요 수 내림차순으로 정렬하고, 동률이면 상품 ID 오름차순으로 정렬한다.
- `viewerType=user`인 경우 섹션은 해당 섹션에 포함된 단일 테스트 사용자의 좋아요 상품 수 내림차순으로 정렬한다.
- `viewerType=user`인 경우 섹션 좋아요 상품 수가 같으면 기존 섹션 노출 순서를 따른다.
- `viewerType=user`인 경우 셀렉션은 해당 셀렉션에 포함된 단일 테스트 사용자의 좋아요 상품 수 내림차순으로 정렬한다.
- `viewerType=user`인 경우 셀렉션 좋아요 상품 수가 같으면 기존 셀렉션 노출 순서를 따른다.
- `viewerType=user`인 경우 상품은 좋아요된 상품 우선, 좋아요 수 내림차순, 상품 ID 오름차순으로 정렬한다.
- 같은 상품이 여러 셀렉션 또는 섹션에 중복 노출될 수 있다.
- `viewerType=guest`인 경우 모든 상품의 `isLiked=false`로 응답한다.
- `viewerType=user`인 경우 단일 테스트 사용자의 좋아요 상태를 기준으로 `isLiked`를 응답한다.
- ShowCase 데이터는 포함하지 않는다.
- cursor는 요청 `viewerType`에 종속된다. `guest` cursor를 `user` 요청에 사용하거나 반대로 사용하는 경우 잘못된 cursor로 보고 `400 GLB-E001`을 반환한다.
- `viewerType=user` 요청 중 좋아요 상태가 변경되면 cursor pagination의 정렬 snapshot 일관성은 보장하지 않는다.
- 상품의 `price`는 최종 판매가를 의미한다.
- 상품의 `saleRate`는 할인율을 의미하며, 할인 정보가 없으면 `0`으로 응답한다.
- 현재 범위에서는 상품의 정가를 의미하는 `originalPrice`를 응답하지 않는다.
- 상품의 `tags`는 서버가 의미를 해석하지 않는 seed 기반 자유 문자열 배열이다.
- 상품 태그는 기획에서 전달한 순서대로 응답한다.

### 응답 데이터

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `shortcuts` | Array | 카테고리 숏컷 목록 |
| `shortcuts[].shortcutId` | Long | 숏컷 ID |
| `shortcuts[].name` | String | 숏컷 이름 |
| `shortcuts[].imageUrl` | String | 숏컷 이미지 URL |
| `shortcuts[].categoryId` | Long | 연결된 카테고리 ID |
| `sections` | Array | 홈 큐레이션 섹션 목록 |
| `sections[].sectionId` | Long | 홈 섹션 ID |
| `sections[].title` | String | 홈 섹션 제목 |
| `sections[].description` | String | 홈 섹션 설명 |
| `sections[].heroImageUrl` | String | 홈 섹션 대표 이미지 URL |
| `sections[].selections` | Array | 섹션 우측 셀렉션 목록 |
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
| `products[].tags` | Array<String> | 상품 카드 태그 목록 |
| `products[].likeCount` | Integer | 상품 좋아요 수 |
| `products[].isLiked` | Boolean | 상품 좋아요 여부 |
| `pageInfo.nextCursor` | String or null | 다음 요청에 사용할 cursor |
| `pageInfo.hasNext` | Boolean | 다음 페이지 존재 여부 |
| `pageInfo.size` | Integer | 요청 또는 적용된 페이지 크기 |

### 예외

| 상황 | HTTP Status | Error Code |
| --- | --- | --- |
| 잘못된 `viewerType` | 400 | `GLB-E001` |
| 잘못된 `cursor` | 400 | `GLB-E001` |
| `size` 범위 위반 | 400 | `GLB-E001` |
| 서버 내부 오류 | 500 | `GLB-E005` |

### 빈 상태

- 노출 가능한 숏컷이 없으면 `shortcuts=[]`를 반환한다.
- 노출 가능한 섹션이 없으면 `sections=[]`, `pageInfo.hasNext=false`, `pageInfo.nextCursor=null`을 반환한다.
- 특정 셀렉션에 상품이 없으면 해당 셀렉션의 `products=[]`를 반환할 수 있다.

### 6.3 쇼케이스 피드 조회

### 목적

ShowCase 화면의 상단 featured 카드와 테마별 쇼케이스 피드 목록을 조회한다.

### API

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/showcases` |
| Auth | 미사용 |

### 입력

| Query | 타입 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- | --- |
| `theme` | String | No | 전체 | 특정 테마만 조회할 때 사용 |
| `cursor` | String | No | 없음 | 다음 페이지 조회 기준 cursor |
| `size` | Integer | No | `12` | 한 번에 조회할 쇼케이스 개수. 최소 `1`, 최대 `50` |

### 처리 규칙

- 상단 featured 영역과 하단 theme section 영역을 분리해 응답한다.
- ShowCase는 섹션 단위로 고정 노출한다.
- ShowCase 목록에는 개인화 정렬을 적용하지 않는다.
- ShowCase 카드에는 좋아요를 두지 않는다.
- 날짜는 원본 날짜만 응답한다.
- 날짜는 현재 표시용으로 사용하되, 추후 필터 기준으로 확장할 수 있도록 보관한다.
- featured와 일반 쇼케이스는 같은 ShowCase 테이블에서 관리한다.
- 기획 전달 JSON에서 `isFeatured`로 featured를 명시하면 해당 쇼케이스를 featured로 사용한다.
- `isFeatured`가 없으면 전체 ShowCase의 index 기준 마지막 2개를 featured로 선정한다.
- featured 응답 순서는 기존 노출 순서를 유지한다.
- featured 항목이 일반 쇼케이스에 중복 포함되는 것은 허용한다.
- featured는 `cursor`가 없는 첫 요청에서만 응답한다.
- `cursor`가 있는 후속 요청에서는 `featured=[]`로 응답한다.
- 일반 `showcases`는 featured 포함 여부와 무관하게 전체 ShowCase를 대상으로 pagination한다.
- 응답 배열은 서버가 정한 노출 순서를 따른다.
- `displayOrder`는 서버 내부 정렬 및 seed 관리를 위한 필드로 사용하고, API 응답에는 노출하지 않는다.
- ShowCase section 응답에는 현재 `description`을 포함하지 않는다.

### 응답 데이터

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `featured` | Array | 상단 featured 쇼케이스 목록 |
| `featured[].showcaseId` | Long | featured 쇼케이스 ID |
| `featured[].title` | String | featured 쇼케이스 제목 |
| `featured[].description` | String | featured 쇼케이스 설명 |
| `featured[].imageUrl` | String | featured 쇼케이스 이미지 URL |
| `sections` | Array | 테마별 쇼케이스 섹션 목록 |
| `sections[].sectionId` | Long | 쇼케이스 섹션 ID |
| `sections[].theme` | String | 섹션 테마 |
| `sections[].title` | String | 섹션 제목 |
| `sections[].showcases` | Array | 섹션 하위 쇼케이스 목록 |
| `sections[].showcases[].showcaseId` | Long | 쇼케이스 ID |
| `sections[].showcases[].title` | String | 쇼케이스 제목 |
| `sections[].showcases[].description` | String | 쇼케이스 설명 |
| `sections[].showcases[].imageUrl` | String | 쇼케이스 이미지 URL |
| `sections[].showcases[].startDate` | LocalDate | 쇼케이스 시작일 |
| `sections[].showcases[].endDate` | LocalDate | 쇼케이스 종료일 |
| `pageInfo.nextCursor` | String or null | 다음 요청에 사용할 cursor |
| `pageInfo.hasNext` | Boolean | 다음 페이지 존재 여부 |
| `pageInfo.size` | Integer | 요청 또는 적용된 페이지 크기 |

### 예외

| 상황 | HTTP Status | Error Code |
| --- | --- | --- |
| 지원하지 않는 `theme` | 400 | `GLB-E001` |
| 잘못된 `cursor` | 400 | `GLB-E001` |
| `size` 범위 위반 | 400 | `GLB-E001` |
| 서버 내부 오류 | 500 | `GLB-E005` |

### 빈 상태

- 노출 가능한 featured가 없으면 `featured=[]`를 반환한다.
- 노출 가능한 섹션이 없으면 `sections=[]`, `pageInfo.hasNext=false`, `pageInfo.nextCursor=null`을 반환한다.
- 특정 섹션에 쇼케이스가 없으면 해당 섹션의 `showcases=[]`를 반환할 수 있다.

### 6.4 상품 좋아요 토글

### 목적

Main 화면 상품 카드의 좋아요 상태를 토글한다.

### API

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/products/{productId}/like` |
| Auth | 미사용 |

### 입력

| Path Variable | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `productId` | Long | Yes | 좋아요 토글 대상 상품 ID |

| Query | 타입 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- | --- |
| `viewerType` | String | No | `guest` | `user`, `guest`만 허용 |

### 처리 규칙

- `viewerType=user`인 경우에만 좋아요 토글을 수행한다.
- `viewerType=guest` 또는 미전달인 경우 좋아요 상태를 변경하지 않고 `403 PRODUCT-E002`로 응답한다.
- 실제 인증 기능은 없으므로 `viewerType=user`는 단일 테스트 사용자를 의미한다.
- 단일 테스트 사용자는 User 테이블에 `id=1`로 seed 등록한다.
- `viewerType=user` 요청은 항상 `user_id=1` 기준으로 처리한다.
- 좋아요 토글 후 변경된 `isLiked`, `likeCount`를 반환한다.
- 좋아요 수는 토글 결과에 따라 실제로 증가 또는 감소한다.
- 좋아요 수는 0 미만이 될 수 없다.

### 응답 데이터

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `productId` | Long | 상품 ID |
| `isLiked` | Boolean | 토글 후 좋아요 여부 |
| `likeCount` | Integer | 토글 후 좋아요 수 |

### 예외

| 상황 | HTTP Status | Error Code |
| --- | --- | --- |
| 잘못된 `viewerType` | 400 | `GLB-E001` |
| 비로그인 사용자 요청 | 403 | `PRODUCT-E002` |
| 존재하지 않는 상품 | 404 | `PRODUCT-E001` |
| 서버 내부 오류 | 500 | `GLB-E005` |

### 빈 상태

해당 없음.

### 6.5 카테고리 메가 메뉴 조회

### 목적

Header의 카테고리 메가 메뉴에서 사용할 대분류, 중분류, 세부 카테고리 목록을 조회한다.

### API

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/nav` |
| Auth | 미사용 |

### 입력

별도 header, query string, request body를 사용하지 않는다.

### 처리 규칙

- 대분류, 중분류, 세부 카테고리를 한 번에 반환한다.
- seed에 포함된 카테고리는 노출 대상 카테고리로 간주한다.
- 응답 배열은 서버가 정한 노출 순서를 따른다.
- 홈 숏컷 카테고리와 메가 메뉴 카테고리는 같은 Category 데이터를 참조한다.
- 홈 숏컷 API 응답과 카테고리 메가 메뉴 API 응답에 카테고리 정보가 중복되는 것은 의도된 설계다.
- `Shopping`, `Special-Order`, `Showcase`, `PT`, `29Magazine` 같은 1차 글로벌 메뉴는 본 API 범위에서 제외한다.
- 대분류 예시 값은 seed 데이터 기준으로 고정한다.

### 응답 데이터

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `categories` | Array | 대분류 목록 |
| `categories[].topCategoryId` | Long | 대분류 ID |
| `categories[].name` | String | 대분류 이름 |
| `categories[].middleCategories` | Array | 중분류 목록 |
| `middleCategories[].middleCategoryId` | Long | 중분류 ID |
| `middleCategories[].name` | String | 중분류 이름 |
| `middleCategories[].subCategories` | Array | 세부 카테고리 목록 |
| `subCategories[].subCategoryId` | Long | 세부 카테고리 ID |
| `subCategories[].name` | String | 세부 카테고리 이름 |

### 예외

| 상황 | HTTP Status | Error Code |
| --- | --- | --- |
| 서버 내부 오류 | 500 | `GLB-E005` |

### 빈 상태

노출 가능한 카테고리가 없으면 `categories=[]`를 반환한다.

### 6.6 푸터 공지 조회

### 목적

Footer의 NOTICE 영역에 노출할 공지 목록을 조회한다.

### API

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/notices` |
| Auth | 미사용 |

### 입력

별도 header, query string, request body를 사용하지 않는다.

### 처리 규칙

- seed에 포함된 모든 공지는 노출 가능한 공지로 간주한다.
- seed 데이터의 index를 공지 순서로 사용한다.
- index 기준 가장 최근 5개 공지를 반환한다.
- 응답 배열은 공지 순서대로 정렬한다.
- 공지 클릭 이동은 제공하지 않으므로 `linkUrl`은 응답에 포함하지 않는다.
- 고객센터, SNS, 사업자 정보는 본 API 범위에서 제외한다.

### 응답 데이터

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `notices` | Array | 푸터 공지 목록 |
| `notices[].noticeId` | Long | 공지 ID |
| `notices[].title` | String | 공지 제목 |

### 예외

| 상황 | HTTP Status | Error Code |
| --- | --- | --- |
| 서버 내부 오류 | 500 | `GLB-E005` |

### 빈 상태

노출 가능한 공지가 없으면 `notices=[]`를 반환한다.

## 7. 비기능 요구사항

### 7.1 기술 스택

| 항목 | 내용 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.5.x |
| Build Tool | Gradle Wrapper |
| Database | MySQL |
| ORM | Spring Data JPA |
| API Docs | SpringDoc OpenAPI |
| Validation | Spring Validation |

### 7.2 API 문서화

- Swagger UI를 제공한다.
- Controller에는 `@Tag`를 작성한다.
- API 메서드에는 `@Operation`을 작성한다.
- DTO에는 `@Schema`를 작성한다.
- 요청 값 검증에는 Bean Validation을 사용한다.

### 7.3 예외 처리

- 공통 예외 처리는 `GlobalExceptionHandler`에서 담당한다.
- 도메인 예외는 `BaseException`과 도메인별 `ErrorCode` 조합을 기본으로 한다.
- Validation 실패는 `400 GLB-E001`로 응답한다.

## 8. ERD 설계 시 고려사항

요구사항 기준으로 다음 도메인 후보를 검토한다.

| 도메인 | 설계 고려사항 |
| --- | --- |
| Category | top, middle, sub 계층 구조 필요 |
| HomeShortcut | Category를 참조하되 홈 전용 이미지와 노출 순서는 별도 관리 |
| HomeCarousel | 이미지, 대체 텍스트, 링크, 노출 순서 |
| HomeSection | 홈 피드의 대표 섹션 |
| HomeSelection | 섹션 내부 셀렉션 카드 |
| Product | 상품 카드 기본 정보. `price`는 최종 판매가, `saleRate`는 할인율 |
| ProductTag | 상품 카드 태그. 자유 문자열과 노출 순서 관리 |
| User | 단일 테스트 사용자. seed ID는 `1`로 고정 |
| SelectionProduct | 셀렉션과 상품의 연결 및 노출 순서 |
| ProductLike | 단일 테스트 사용자 기준 좋아요 상태 |
| ShowCase | 쇼케이스 카드 기본 정보. featured 여부와 피드 index 또는 노출 순서 관리 |
| ShowCaseSection | 테마별 섹션 정보. 현재 API 응답에는 description 제외 |
| Notice | 푸터 공지. 활성 플래그 없이 seed index 기준 최신 5개 노출 |

## 9. 확인 필요 사항

아래 항목은 후속 설계에서 확정이 필요하다.

| 항목 | 현재 가정 | 확인 필요 내용 |
| --- | --- | --- |
| 이미지 전달 및 업로드 | seed에는 S3 객체 키 사용, 응답은 presigned URL 사용 | S3 업로드 담당자와 bucket/path 규칙 확인 |

## 10. 후속 산출물

본 문서 이후 작성할 문서는 다음과 같다.

1. ERD 및 도메인 모델 설계서
2. API 구현 설계서
3. DB seed 데이터 설계서
4. 더미데이터 전달 양식
