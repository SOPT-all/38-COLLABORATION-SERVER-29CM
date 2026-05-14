# API 구현 설계서

## 1. 문서 목적

이 문서는 29CM Collaboration Server의 API 구현 방향을 정리한다.

범위는 다음과 같다.

- API별 Controller, Service, Repository 책임 분리
- 공통 파라미터, cursor, viewerType, 응답/예외 처리 방식
- ERD를 실제 JPA 엔티티와 조회 흐름으로 옮길 때의 기준
- 구현 시 주의해야 할 데이터 정렬, 좋아요, seed 정책

본 문서는 구현 가이드이며 API 계약의 SSoT가 아니다.
API path, request, response, HTTP status, error code는 팀스페이스 Notion API 명세서를 우선한다.
AI 또는 로컬 개발자가 팀스페이스 Notion에 접근할 수 없는 경우 `docs/api/API_SPEC_SNAPSHOT.md`를 dated snapshot으로 참고한다.

## 2. 기준 문서

| 문서 | 역할 |
| --- | --- |
| `docs/api/API_SPEC_SNAPSHOT.md` | API 계약의 로컬 스냅샷 |
| `docs/spec/REQUIREMENTS.md` | 제품/서버 요구사항 |
| `docs/erd/ERD.md` | ERD 설계 설명 |
| `docs/erd/29cm.dbml` | DBML 원본 |
| `AGENTS.md` | AI 작업 시 문서 확인 순서와 규칙 |

충돌 처리 기준:

1. API 계약은 팀스페이스 Notion API 명세서를 우선한다.
2. 제품/서버 정책은 `docs/spec/REQUIREMENTS.md`를 우선한다.
3. 테이블/관계/컬럼은 `docs/erd/29cm.dbml`을 우선한다.
4. 본 문서는 위 문서를 구현으로 옮기는 방법을 설명한다.

## 3. 구현 범위

구현 대상 API는 다음 6개다.

| 기능 | Method | Path |
| --- | --- | --- |
| 홈 캐러셀 조회 | GET | `/api/v1/home/carousels` |
| 홈 메인 조회 | GET | `/api/v1/home` |
| 쇼케이스 피드 조회 | GET | `/api/v1/showcases` |
| 상품 좋아요 토글 | PATCH | `/api/v1/products/{productId}/like` |
| 카테고리 메가 메뉴 조회 | GET | `/api/v1/nav` |
| 푸터 공지 조회 | GET | `/api/v1/notices` |

제외 대상:

- 인증/인가
- 이미지 업로드 API
- 관리자 기능
- ShowCase Detail
- Category Product List
- Brand Detail
- 주문, 결제, 장바구니

## 4. 패키지 구조

패키지 구조는 로컬 위키 컨벤션을 따른다.

도메인 단위 패키지 구성을 기본으로 하며, 각 도메인은 다음 구조를 사용한다.

```text
org.sopt.domain
  category
    controller
    service
    repository
    domain
    dto
      request
      response
    exception
    code
  home
    controller
    service
    repository
    domain
    dto
      request
      response
    exception
    code
  notice
    controller
    service
    repository
    domain
    dto
      request
      response
    exception
    code
  product
    controller
    service
    repository
    domain
    dto
      request
      response
    exception
    code
  showcase
    controller
    service
    repository
    domain
    dto
      request
      response
    exception
    code
  user
    controller
    service
    repository
    domain
    dto
      request
      response
    exception
    code
```

공통 유틸과 정책성 타입은 `global` 하위에 둔다.
`global.support`는 특정 도메인에 속하지 않는 공통 정책 유틸을 모으기 위한 패키지다.

```text
org.sopt.global
  code
  config
  entity
  exception
  response
  support
    cursor
    pagination
    viewer
```

현재 API 범위에서 request body를 받는 API는 없으므로 `dto/request`는 두지 않는다.
도메인별 예외나 코드가 필요 없는 도메인은 `exception`, `code` 패키지를 두지 않는다.
Lombok 사용 범위는 로컬 위키의 코드 스타일을 따른다.

## 5. 계층별 책임

### Controller

- API path와 HTTP method를 매핑한다.
- Query string, path variable, request body를 받는다.
- Bean Validation으로 단순 범위 검증을 처리한다.
- 응답은 `CommonApiResponse.successResponse(GlobalSuccessCode.OK, data)` 형태로 감싼다.
- Swagger 문서화를 위해 Controller에는 `@Tag`, API 메서드에는 `@Operation`, parameter에는 `@Parameter`를 사용한다.
- 비즈니스 판단은 Service로 넘긴다.

### Service

- API별 유스케이스를 구현한다.
- viewerType, cursor, size 등 공통 정책을 적용한다.
- 필요한 Repository 조회를 조합한다.
- 도메인 예외를 발생시킨다.
- 트랜잭션 경계를 가진다.

조회 API는 기본적으로 `@Transactional(readOnly = true)`를 사용한다.
좋아요 토글 API는 `@Transactional`을 사용한다.

### Repository

- JPA Repository를 기본으로 사용한다.
- API 응답 조립에 필요한 데이터를 정렬 조건과 함께 조회한다.
- N+1 문제가 예상되는 조회는 `IN` 절 기반 batch 조회를 기본으로 사용한다.
- 단일 엔티티와 그에 직접 연결된 연관 엔티티만 함께 가져오는 경우에는 fetch join을 사용한다.

구현은 불필요한 반복 쿼리를 만들지 않도록 작성한다.

### DTO

- Controller 응답 DTO는 Java `record` 사용을 우선한다.
- API 명세서의 필드명과 타입을 그대로 따른다.
- Entity를 API 응답으로 직접 노출하지 않는다.
- Swagger 문서화를 위해 `@Schema`를 작성한다.

### CORS

CORS 설정은 현재 API 구현 설계 범위에서 제외한다.
브라우저 클라이언트 연동 시 CORS가 필요해지면 로컬 위키의 설정 컨벤션에 따라 별도 `config` 클래스로 추가한다.

## 6. 공통 구현 정책

### 6.1 공통 응답

응답 envelope, 성공/에러 코드, 빈 응답 처리 등 공통 응답 계약은 `docs/api/API_SPEC_SNAPSHOT.md` §3·§4를 따른다.
현재 글로벌 응답 타입은 다음 형태를 사용한다.

```java
CommonApiResponse<T>(
    String code,
    boolean success,
    String message,
    T data
)
```

구현 결정:

- 공통 envelope의 `data=null`은 생략될 수 있지만, `data` 내부 DTO의 nullable 필드는 명세대로 `null`을 유지한다.
- 예를 들어 `pageInfo.nextCursor`는 값이 없을 때 필드를 생략하지 않고 `null`로 직렬화한다.
- 구현 시 nullable 필드를 가진 응답 DTO 또는 해당 필드에 `@JsonInclude(JsonInclude.Include.ALWAYS)`를 적용한다.
- 공통 응답의 `@JsonInclude(JsonInclude.Include.NON_NULL)` 정책을 전역으로 해제하지 않는다.
- Bean Validation 실패 시에는 `data`에 `{필드명: 메시지}` 형태의 map을 포함한다.

### 6.2 예외 처리

도메인 예외는 `BaseException`과 도메인별 `ErrorCode` 조합을 사용한다.
도메인 에러 코드와 사용 시점은 `docs/api/API_SPEC_SNAPSHOT.md` §4를 따른다.

`CATEGORY-E001`은 현재 구현 대상 6개 API에서는 사용하지 않는다.
`/api/v1/nav`는 seed된 전체 카테고리를 조회하므로 카테고리 데이터가 없어도 에러가 아니라 `categories=[]` 성공 응답을 반환한다.

잘못된 query/path parameter, validation 실패, cursor decode 실패, 허용되지 않은 theme/viewerType은 `GLB-E001`로 처리한다.
`GlobalExceptionHandler`는 `MethodArgumentTypeMismatchException`도 `400 GLB-E001`로 처리한다.
예를 들어 `size=abc`처럼 Spring MVC 바인딩 단계에서 실패하는 요청도 공통 잘못된 요청 응답으로 내린다.
잘못된 HTTP method 요청은 본 범위에서 별도 처리하지 않는다.

### 6.3 viewerType

`viewerType` 계약(허용 값, 기본값, `isLiked` 처리, 좋아요 토글에서의 동작)은 `docs/api/API_SPEC_SNAPSHOT.md` §5를 따른다.

구현 방향:

- `ViewerType` enum을 둔다.
- 문자열 파싱은 `org.sopt.global.support.viewer.ViewerTypeResolver`에서만 수행한다.
- 기본 메서드는 `ViewerType resolve(String rawViewerType)` 형태로 둔다.
- `rawViewerType`이 `null`이면 `ViewerType.GUEST`를 반환한다.
- 허용되지 않은 값이면 `BaseException(GlobalErrorCode.INVALID_REQUEST)`을 발생시킨다.
- Controller는 enum 변환을 직접 하지 않고, Service 진입 전에 정책 유틸을 사용한다.

### 6.4 cursor

cursor 계약(opaque, Base64, 클라이언트 해석 금지, 다음 데이터 없을 때 처리)은 `docs/api/API_SPEC_SNAPSHOT.md` §5를 따른다.
서버 내부 기본 기준은 `displayOrder + id`다.
홈 메인 조회는 `viewerType`에 따라 cursor payload와 정렬 기준이 달라진다.

구현 방향:

- `CursorCodec` 또는 유사한 공통 컴포넌트를 둔다.
- Base64 인코딩/디코딩 로직은 공통으로 재사용한다.
- payload 필드명은 API 명세서 예시와 맞춰 도메인별로 둔다.
- Home guest cursor payload는 `viewerType`, `displayOrder`, `sectionId`를 포함한다.
- Home user cursor payload는 `viewerType`, `likedProductCount`, `displayOrder`, `sectionId`를 포함한다.
- Home cursor의 `viewerType`이 요청 `viewerType`과 다르면 잘못된 cursor로 보고 `400 GLB-E001`을 반환한다.
- ShowCase cursor payload는 `sectionDisplayOrder`, `sectionId`, `showcaseDisplayOrder`, `showcaseId`를 포함한다.
- 잘못된 Base64 문자열, JSON 파싱 실패, 필수 필드 누락, 음수 값, 현재 API와 다른 payload 형식은 잘못된 cursor로 보고 `400 GLB-E001`을 반환한다.

### 6.5 pagination

`size` 기본값과 허용 범위, `pageInfo` 응답 형태는 `docs/api/API_SPEC_SNAPSHOT.md` §5·§8·§9를 따른다.
cursor 기반 API는 `size + 1`개를 조회해 다음 페이지 여부를 판단한다.
Notice는 cursor pagination 대상이 아니며, 고정으로 최신 5건만 조회한다.

정렬:

- `display_order ASC, id ASC`를 사용한다. (`display_order`는 작을수록 먼저 노출되는 수동 노출 순서)
- Home user 섹션 정렬은 `likedProductCount DESC, display_order ASC, id ASC`를 사용한다.

다음 페이지 조회 조건은 표준 keyset pagination 형태를 사용한다.

```sql
WHERE display_order > :cursorDisplayOrder
   OR (display_order = :cursorDisplayOrder AND id > :cursorId)
ORDER BY display_order ASC, id ASC
LIMIT :size + 1
```

Home user 섹션 pagination은 `likedProductCount DESC` 정렬이 섞이므로 다음 조건을 사용한다.

```sql
WHERE liked_product_count < :cursorLikedProductCount
   OR (
        liked_product_count = :cursorLikedProductCount
        AND (
            display_order > :cursorDisplayOrder
            OR (display_order = :cursorDisplayOrder AND id > :cursorSectionId)
        )
   )
ORDER BY liked_product_count DESC, display_order ASC, id ASC
LIMIT :size + 1
```

`viewerType=user` 요청 중 좋아요 상태가 변경되면 cursor pagination의 정렬 snapshot 일관성은 보장하지 않는다.

공통 페이지 정보 응답은 다음 형태를 사용한다.

```java
public record PageInfoResponse(
        String nextCursor,
        boolean hasNext,
        int size
) {
}
```

## 7. 엔티티 구현 기준

### 7.1 시간 컬럼

ERD 정책:

- `created_at`은 현재 `products`, `product_likes`에만 둔다.
- `updated_at`은 실제 변경이 발생하는 테이블에만 둔다.
- 현재 `updated_at`이 필요한 테이블은 `products`다.

구현 방향:

- `Product`는 기존 `BaseTimeEntity`를 상속한다.
- `ProductLike`는 `createdAt` 필드만 직접 선언한다.
- `ProductLike.createdAt`에는 `@CreatedDate`, `@EntityListeners(AuditingEntityListener.class)`를 적용한다.
- 그 외 seed성 엔티티에는 시간 컬럼을 두지 않는다.
- 별도 created-only base entity는 현재 범위에서 추가하지 않는다.

### 7.2 관계 매핑

기본 원칙:

- 연관관계는 `LAZY`를 기본으로 한다.
- API 응답 조립에 필요하지 않은 양방향 관계는 만들지 않는다.
- 삭제 전파(`cascade`)와 `orphanRemoval`은 사용하지 않는다.
- 다대다 직접 매핑은 사용하지 않고 연결 테이블 엔티티를 둔다.

주요 관계:

- `Category` self-reference
- `HomeShortcut -> Category`
- `HomeSelection -> HomeSection`
- `SelectionProduct -> HomeSelection`
- `SelectionProduct -> Product`
- `ProductTag -> Product`
- `ProductLike -> User`
- `ProductLike -> Product`
- `Showcase -> ShowcaseSection`

## 8. API별 구현 설계

### 8.1 홈 캐러셀 조회

대상:

- `GET /api/v1/home/carousels`

흐름:

1. Controller가 요청을 받는다.
2. Service가 전체 홈 캐러셀을 조회한다.
3. Repository는 `display_order ASC, id ASC`로 정렬한다.
4. Service가 응답 DTO로 변환한다.

필요 컴포넌트:

- `HomeController`
- `HomeCarouselService`
- `HomeCarouselRepository`
- `HomeCarouselListResponse`
- `HomeCarouselResponse`

### 8.2 홈 메인 조회

대상:

- `GET /api/v1/home`

흐름:

1. `viewerType`을 파싱한다.
2. `size` 범위를 검증한다.
3. cursor가 있으면 decode한다.
4. 홈 숏컷을 `display_order ASC, id ASC`로 조회한다.
5. `viewerType=guest`이면 홈 섹션을 `display_order ASC, id ASC` 기준 cursor로 `size + 1`개 조회한다.
6. `viewerType=user`이면 홈 섹션을 단일 테스트 사용자의 섹션별 좋아요 상품 수 기준 cursor로 `size + 1`개 조회한다.
7. 응답에 포함될 섹션 ID 목록을 확정한다.
8. 섹션 하위 셀렉션을 batch 조회한다.
9. 셀렉션 하위 상품, 상품 태그, 좋아요 상태를 batch 조회한다.
10. `viewerType`에 맞춰 셀렉션과 상품을 정렬한다.
11. `pageInfo`를 생성한다.
12. 응답 DTO를 조립한다.

정렬:

- guest 섹션: `home_sections.display_order ASC, id ASC`
- guest 셀렉션: `home_selections.display_order ASC, id ASC`
- guest 상품: `products.like_count DESC`, 동률이면 `products.id ASC`
- user 섹션: 섹션에 포함된 좋아요 상품 수 `DESC`, 동률이면 `home_sections.display_order ASC, id ASC`
- user 셀렉션: 셀렉션에 포함된 좋아요 상품 수 `DESC`, 동률이면 `home_selections.display_order ASC, id ASC`
- user 상품: 좋아요된 상품 우선, `products.like_count DESC`, `products.id ASC`
- 태그: `product_tags.display_order ASC, id ASC`

`viewerType=user`는 섹션, 셀렉션, 상품 정렬과 `isLiked` 계산에 사용한다.
섹션의 좋아요 상품 수는 같은 상품이 여러 셀렉션에 중복 노출될 수 있으므로 distinct product ID 기준으로 계산한다.

좋아요 상태:

- guest: 모든 상품 `isLiked=false`
- user: `product_likes`에서 `user_id=1` 기준 좋아요 상품 ID를 조회해 계산

필요 컴포넌트:

- `HomeController`
- `HomeService`
- `HomeSectionRepository`
- `HomeSelectionRepository`
- `HomeShortcutRepository`
- `SelectionProductRepository`
- `ProductTagRepository`
- `ProductLikeRepository`
- `HomeMainResponse`
- `PageInfoResponse`

### 8.3 쇼케이스 피드 조회

대상:

- `GET /api/v1/showcases`

흐름:

1. `theme`이 있으면 seed된 `showcase_sections.theme` 값인지 검증한다.
2. `size` 범위를 검증한다.
3. cursor가 있으면 decode한다.
4. cursor가 없으면 featured 목록을 조회한다.
5. 일반 쇼케이스는 featured 여부와 무관하게 전체 쇼케이스 대상으로 pagination한다.
6. theme이 있으면 해당 theme의 쇼케이스만 조회한다.
7. 쇼케이스를 section 단위로 묶는다.
8. `pageInfo`를 생성한다.
9. 응답 DTO를 조립한다.

theme 검증:

- 허용 가능한 `theme` 값은 `showcase_sections.theme`에 seed된 값이다.
- enum 하드코딩은 하지 않는다.
- 요청마다 `showcase_sections` 기준 exists 조회로 검증한다.
- 존재하지 않는 `theme`이면 `400 GLB-E001`을 반환한다.
- 별도 application cache는 두지 않는다.

cursor와 theme:

- ShowCase cursor payload에는 `sectionDisplayOrder`, `sectionId`, `showcaseDisplayOrder`, `showcaseId`를 포함한다.
- `theme`은 cursor에 인코딩하지 않는다.
- 클라이언트가 중간에 `theme`을 바꾸면 페이지 연속성은 보장하지 않는다.
- theme 변경 시 클라이언트는 cursor를 초기화하고 첫 페이지부터 다시 요청한다.

featured 정책:

- `is_featured=true` 데이터가 있으면 해당 데이터를 사용한다.
- `is_featured=true` 데이터가 없으면 전체 쇼케이스 중 `display_order` 기준 마지막 2개를 사용한다.
- fallback featured는 `display_order DESC, id DESC LIMIT 2`로 최신 2개를 선택한 뒤, 응답 조립 시 `display_order ASC, id ASC`로 재정렬해 기존 노출 순서를 유지한다.
- cursor가 있는 후속 요청에서는 `featured=[]`
- featured와 일반 쇼케이스 중복은 허용한다.

정렬:

- 일반 쇼케이스 pagination: `showcase_sections.display_order ASC, showcase_sections.id ASC, showcases.display_order ASC, showcases.id ASC`
- fallback featured 선택: `display_order DESC, id DESC LIMIT 2`
- fallback featured 응답: 선택된 2개를 `display_order ASC, id ASC`로 재정렬

필요 컴포넌트:

- `ShowcaseController`
- `ShowcaseService`
- `ShowcaseRepository`
- `ShowcaseSectionRepository`
- `ShowcaseItemResponse`
- `PageInfoResponse`

### 8.4 상품 좋아요 토글

대상:

- `POST /api/v1/products/{productId}/like`

흐름:

1. `viewerType` 문자열을 검증하고 파싱한다.
2. 허용되지 않은 값이면 `400 GLB-E001`을 발생시킨다.
3. `viewerType`이 미전달되었거나 `guest`이면 `403 PRODUCT-E002`를 발생시킨다.
4. 상품을 조회한다.
5. 상품이 없으면 `404 PRODUCT-E001`을 발생시킨다.
6. `user_id=1`, `product_id` 기준 좋아요 row 존재 여부를 조회한다.
7. row가 있으면 삭제하고 `like_count`를 1 감소시킨다.
8. row가 없으면 생성하고 `like_count`를 1 증가시킨다.
9. 변경 후 `isLiked`, `likeCount`를 응답한다.

트랜잭션:

- 전체 흐름은 하나의 `@Transactional` 안에서 처리한다.
- `Product` 엔티티를 조회한 뒤 도메인 메서드로 `like_count`를 변경하고 JPA dirty checking으로 반영한다.
- 도메인 메서드는 `increaseLikeCount()`, `decreaseLikeCount()`처럼 의도를 드러내는 이름을 사용한다.
- `like_count`는 0 미만이 되지 않도록 도메인 메서드에서 방어한다.
- 동시성 제어(pessimistic lock, 조건부 update)는 본 범위 외다.

필요 컴포넌트:

- `ProductLikeController`
- `ProductLikeService`
- `ProductRepository`
- `ProductLikeRepository`
- `ProductLikeToggleResponse`
- `ProductErrorCode`

### 8.5 카테고리 메가 메뉴 조회

대상:

- `GET /api/v1/nav`

흐름:

1. 전체 카테고리를 조회한다.
2. `depth`, `parent_id`, `display_order`, `id` 기준으로 계층을 조립한다.
3. depth 1 카테고리를 top category로 응답한다.
4. depth 2는 middle category, depth 3은 sub category로 응답한다.

정렬:

- 같은 부모 아래에서 `display_order ASC, id ASC`

필요 컴포넌트:

- `NavController`
- `CategoryService`
- `CategoryRepository`
- `NavCategoryResponse`
- `TopCategoryResponse`
- `MiddleCategoryResponse`
- `SubCategoryResponse`

주의:

- `Shopping`, `Special-Order`, `Showcase`, `PT`, `29Magazine` 같은 글로벌 메뉴는 seed에 넣지 않는다.
- 홈 숏컷과 메가 메뉴는 같은 Category를 참조할 수 있다.

### 8.6 푸터 공지 조회

대상:

- `GET /api/v1/notices`

흐름:

1. Notice 최신 5개를 조회한다.
2. 응답 DTO로 변환한다.

정렬:

- `display_order ASC, id ASC`
- `display_order`는 작을수록 먼저 노출되는 수동 노출 순서이며, 푸터는 앞쪽 5개를 반환한다.

필요 컴포넌트:

- `NoticeController`
- `NoticeService`
- `NoticeRepository`
- `NoticeResponse`

## 9. DB seed 구현 기준

seed는 `src/main/resources/db/seed/*.sql`에 도메인 또는 의존 순서별로 분리해 관리한다.
루트 `data.sql`은 사용하지 않는다.

로컬 환경 설정:

- Spring Boot에서 JPA DDL 생성 이후 seed SQL이 실행되도록 `spring.jpa.defer-datasource-initialization=true` 설정을 사용한다.
- 개발 환경에서 항상 seed를 적재하도록 `spring.sql.init.mode=always` 설정을 사용한다.
- seed 파일 위치는 `spring.sql.init.data-locations=optional:classpath*:db/seed/*.sql`로 지정한다.
- `optional:` prefix를 사용해 아직 seed 파일이 없는 브랜치에서도 애플리케이션 실행이 실패하지 않도록 한다.

파일명 규칙:

- FK 참조 순서를 알 수 있도록 숫자 prefix를 둔다.
- 예: `01-users.sql`, `02-categories.sql`, `03-products.sql`, `04-product-tags.sql`, `05-product-likes.sql`, `06-home.sql`, `07-showcases.sql`, `08-notices.sql`
- 새 seed 파일을 추가할 때는 기존 파일의 FK 의존성과 실행 순서를 확인한다.
- 같은 prefix를 두 파일이 공유하지 않도록 한다.

필수 seed:

- `users.id = 1`
- categories
- home_shortcuts
- home_carousels
- home_sections
- home_selections
- products
- product_tags
- selection_products
- product_likes
- showcase_sections
- showcases
- notices

주의:

- FK 참조 순서를 지켜 insert한다.
- `display_order`는 API 응답 순서와 cursor 기준이므로 누락하지 않는다.
- 이미지 컬럼에는 S3 객체 키 문자열을 저장한다.
- 응답 DTO 조립 시 이미지 객체 키는 `S3Service.generatePresignedUrlOrNull()`로 변환한다.
- `products.like_count` 초기값은 화면에 보여줄 운영 누적 좋아요 수 컨셉으로 임의 값을 허용한다.
- 시간 컬럼 seed 값은 `products.created_at`, `products.updated_at`, `product_likes.created_at`에만 입력한다.
- `product_likes` seed는 단일 테스트 사용자 `user_id=1`의 현재 좋아요 상태만 의미한다.
- 따라서 `products.like_count`가 `product_likes` row 수와 같을 필요는 없다.
- 단, `product_likes`에 row가 있는 상품의 `like_count`는 최소 1 이상이어야 자연스럽다.
- 좋아요 토글 시 `like_count`는 현재 값에서 `+1` 또는 `-1`만 적용한다.
- 감소 시 `like_count`는 0 미만이 되지 않아야 한다.

## 10. 테스트 기준

본 절은 테스트 의존성 도입이 결정된 이후의 가이드다.
MockMvc 기반 API 테스트는 `spring-boot-starter-test` 의존성 추가 후 작성한다.

우선순위가 높은 테스트:

- `viewerType` 파싱
- cursor encode/decode 및 잘못된 cursor 처리
- size 범위 검증
- 홈 메인 조회의 상품 정렬과 `isLiked` 계산
- 쇼케이스 featured 첫 페이지/후속 페이지 처리
- 좋아요 토글의 row 생성/삭제와 `like_count` 증감
- Notice 최신 5개 조회

API 테스트는 MockMvc 기반으로 작성한다.
DB seed 기반 통합 테스트를 작성할 경우 테스트 전용 seed와 실제 개발 seed를 분리한다.

## 11. 구현 순서

1. 공통 타입 및 글로벌 예외 처리 보강
   - `ViewerType`
   - `CursorCodec`
   - `PageInfoResponse`
   - `MethodArgumentTypeMismatchException` 핸들러
2. Entity와 Repository 구현
3. Domain ErrorCode 구현
4. 단순 조회 API 구현
   - Home Carousel
   - Notice
   - Nav
5. Product Like 구현
6. Home Main 구현
7. ShowCase 구현
8. seed 데이터 작성
9. Swagger 확인
10. 테스트 의존성 도입 시 MockMvc 확인
