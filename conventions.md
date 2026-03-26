# LetSPT 코드 컨벤션

> 이 문서는 프로젝트 전반의 코딩 규칙을 정의합니다. 모든 코드는 이 컨벤션을 따릅니다.

---

## 목차
1. [주석 규칙](#1-주석-규칙)
2. [패키지 구조](#2-패키지-구조)
3. [네이밍 규칙](#3-네이밍-규칙)
4. [계층별 클래스 규칙](#4-계층별-클래스-규칙)
5. [예외 처리](#5-예외-처리)
6. [Mapper XML](#6-mapper-xml)
7. [데이터베이스 규칙](#7-데이터베이스-규칙)
8. [DTO 규칙](#8-dto-규칙)
9. [Enum 규칙](#9-enum-규칙)

---

## 1. 주석 규칙

**모든 클래스, 메서드, 필드는 반드시 그 위에 설명 주석을 작성한다.**

### 1-1. 클래스 주석
클래스 선언 위에 해당 클래스의 역할을 한 줄 이상 설명한다.

```java
// 게시글 관련 REST API 엔드포인트를 처리하는 컨트롤러
// 인증된 사용자의 게시글 CRUD 요청을 BoardService에 위임한다
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController { ... }
```

```java
// 게시글 생성·조회·수정·삭제 비즈니스 로직을 처리하는 서비스
// 카테고리별 권한 검증 및 작성자 소유권 검사를 수행한다
@Service
@RequiredArgsConstructor
public class BoardService { ... }
```

### 1-2. 메서드 주석
메서드 선언 위에 동작, 파라미터 의미, 예외 조건을 설명한다.

```java
// 게시글 단건 조회
// boardId로 게시글을 조회하며, 삭제된 게시글이거나 존재하지 않으면 BOARD_NOT_FOUND 예외를 던진다
public BoardDetailResponse getDetail(Long boardId) {
    return boardMapper.findById(boardId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
}
```

```java
// 게시글 작성 권한 검증
// LESSON 카테고리는 TRAINER·MASTER만, DIET·EXERCISE는 MEMBER만 작성 가능하다
private void validateWritePermission(BoardCategory category, MemberRole role) { ... }
```

### 1-3. 필드 주석
의미가 명확하지 않은 상수, 설정값, 플래그 필드에는 주석을 단다.

```java
// JWT 토큰 유효기간: 15분 (밀리초 단위)
private static final long ACCESS_TOKEN_EXPIRY = 1000 * 60 * 15;

// 리프레시 토큰 유효기간: 7일
private static final long REFRESH_TOKEN_EXPIRY = 1000 * 60 * 60 * 24 * 7;
```

### 1-4. 인라인 주석
복잡한 조건문이나 비즈니스 규칙이 담긴 코드 옆에 짧게 설명한다.

```java
// 작성자 본인만 수정 가능
if (!board.getAuthorId().equals(author.getMemberId())) {
    throw new BusinessException(ErrorCode.BOARD_ACCESS_DENIED);
}

boardMapper.softDelete(boardId); // 물리 삭제 대신 is_deleted = TRUE로 표시
```

### 1-5. Mapper XML 주석
SQL 쿼리 위에 쿼리의 목적과 중요한 WHERE 조건을 설명한다.

```xml
<!-- 게시글 단건 조회 (삭제된 게시글 제외) -->
<select id="findById" resultMap="boardResultMap">
    SELECT * FROM board
    WHERE board_id = #{boardId}
      AND is_deleted = FALSE
</select>

<!-- 게시글 소프트 삭제: 물리 삭제 대신 is_deleted 플래그와 deleted_at 타임스탬프를 설정 -->
<update id="softDelete">
    UPDATE board
    SET is_deleted = TRUE, deleted_at = CURRENT_TIMESTAMP
    WHERE board_id = #{boardId}
</update>
```

---

## 2. 패키지 구조

기능(feature) 중심으로 패키지를 구성한다. 각 도메인 패키지에 Controller, Service, Mapper, 모델을 함께 둔다.

```
com.aenggukland.letspt/
├── auth/          # 인증 페이지 컨트롤러 (Thymeleaf)
├── board/         # 게시글 도메인: Controller, Service, Mapper, DTO, Enum
├── member/        # 회원 도메인: Controller, Service, Mapper, DTO, Enum
├── security/      # JWT 필터, 토큰 유틸, RefreshToken 엔티티
├── config/        # Spring 설정, GlobalExceptionHandler
├── error/         # 에러 로그 엔티티 및 Mapper
└── exception/     # BusinessException, ErrorCode
```

---

## 3. 네이밍 규칙

### 3-1. 클래스명

| 종류 | 패턴 | 예시 |
|------|------|------|
| 엔티티 | `도메인명` (단수) | `Board`, `Member` |
| 컨트롤러 | `도메인명Controller` | `BoardController` |
| 서비스 | `도메인명Service` | `BoardService` |
| Mapper | `도메인명Mapper` | `BoardMapper` |
| 요청 DTO | `동작명Request` | `BoardCreateRequest`, `BoardUpdateRequest` |
| 응답 DTO | `도메인명Response` | `MemberResponse`, `BoardSummary` |
| Enum | PascalCase | `BoardCategory`, `MemberRole` |
| 예외 | `BusinessException` | 도메인별 구분 없이 ErrorCode로 분기 |

### 3-2. 메서드명

| 목적 | 패턴 | 예시 |
|------|------|------|
| 단건 조회 | `findBy필드명` | `findById`, `findByUsername` |
| 목록 조회 | `findAllBy조건` | `findAllByAuthor`, `findRecentLessonsByMemberId` |
| 저장 | `save` | `save` |
| 수정 | `update` | `update` |
| 소프트 삭제 | `softDelete` | `softDelete` |
| 서비스 조회 | `get설명` | `getDetail`, `getMyInfo` |
| 서비스 생성 | `create` | `create` |
| 권한 검증 | `validate설명` | `validateWritePermission` |
| private 조회 | `get설명` | `getByUsername` |

### 3-3. 변수명

- 로컬 변수: camelCase (`boardId`, `authorId`)
- 상수: UPPER_SNAKE_CASE (`ACCESS_TOKEN_EXPIRY`)
- DB 컬럼: snake_case (`board_id`, `is_deleted`, `created_at`)
- Java ↔ DB: MyBatis가 snake_case ↔ camelCase 자동 변환

---

## 4. 계층별 클래스 규칙

### 4-1. Controller

- `@RestController` + `@RequestMapping("/api/도메인명")`
- `@RequiredArgsConstructor`로 생성자 주입
- 인증된 사용자명은 `@RequestAttribute("username")`으로 수신 (JWT 필터에서 주입)
- 응답 패턴:

```java
// 단건 조회 응답
return ResponseEntity.ok(data);

// 리소스 생성 응답 (본문 없음)
return ResponseEntity.status(HttpStatus.CREATED).build();

// 수정·삭제 응답 (본문 없음)
return ResponseEntity.noContent().build();
```

- 파일 업로드 엔드포인트에는 `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` 명시
- 요청 바디 검증은 `@Valid` 사용

```java
// 게시글 생성: TRAINER·MASTER는 LESSON, MEMBER는 DIET·EXERCISE만 작성 가능
@PostMapping
public ResponseEntity<Void> create(
        @RequestAttribute("username") String username,
        @RequestBody @Valid BoardCreateRequest request) {
    boardService.create(username, request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
}
```

### 4-2. Service

- `@Service` + `@RequiredArgsConstructor`
- 조회 시 항상 Optional로 받아 `.orElseThrow(() -> new BusinessException(ErrorCode.XXX))` 처리
- 권한 검증은 별도 private 메서드로 분리
- 소프트 삭제 전 소유권 검증 필수

```java
// 인증된 사용자명으로 Member 엔티티를 조회하는 공통 헬퍼
// 존재하지 않으면 MEMBER_NOT_FOUND 예외를 던진다
private Member getByUsername(String username) {
    return memberMapper.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
}
```

### 4-3. Mapper (인터페이스)

- `@Mapper` 어노테이션
- 단건 조회는 반드시 `Optional<T>` 반환
- 다중 파라미터에는 `@Param("파라미터명")` 사용
- SQL은 Java 인터페이스에 작성하지 않고 XML에만 작성

```java
// 삭제되지 않은 게시글을 ID로 단건 조회
Optional<Board> findById(Long boardId);

// 작성자 ID와 카테고리 조건으로 게시글 목록 조회
List<Board> findAllByAuthor(@Param("authorId") Long authorId,
                            @Param("category") String category);
```

---

## 5. 예외 처리

### 5-1. BusinessException + ErrorCode

도메인 예외는 모두 `BusinessException`으로 통일하고, 종류는 `ErrorCode` enum으로 구분한다.

```java
// 비즈니스 규칙 위반을 나타내는 단일 예외 클래스
// HTTP 상태 코드와 메시지는 ErrorCode에서 관리한다
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

```java
// 도메인별 에러 코드 정의: HTTP 상태 + 한국어 메시지
public enum ErrorCode {
    // 회원
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // 게시글
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    BOARD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "게시글에 대한 권한이 없습니다."),

    // 서버
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
}
```

### 5-2. GlobalExceptionHandler

`@RestControllerAdvice`로 모든 예외를 중앙 처리한다. 예외 발생 시 `ErrorLogMapper`로 DB에 로그를 남긴다.

---

## 6. Mapper XML

**파일 위치**: `src/main/resources/mapper/XxxMapper.xml`

### 6-1. ResultMap

엔티티마다 `resultMap`을 정의하고, Enum 타입 컬럼에는 `EnumTypeHandler`를 명시한다.

```xml
<!-- Board 엔티티 ResultMap: DB 컬럼(snake_case) → Java 필드(camelCase) 매핑 -->
<resultMap id="boardResultMap" type="com.aenggukland.letspt.board.Board">
    <id property="boardId" column="board_id"/>
    <!-- category는 Enum으로 변환 -->
    <result property="category" column="category"
            typeHandler="org.apache.ibatis.type.EnumTypeHandler"
            javaType="com.aenggukland.letspt.board.BoardCategory"/>
    <result property="isDeleted" column="is_deleted"/>
    <result property="createdAt" column="created_at"/>
</resultMap>
```

### 6-2. INSERT

자동 생성 키는 `useGeneratedKeys="true" keyProperty="엔티티PK필드"` 설정으로 반환받는다.

```xml
<!-- 새 게시글 저장, 생성된 board_id를 엔티티에 자동 주입 -->
<insert id="save" parameterType="com.aenggukland.letspt.board.Board"
        useGeneratedKeys="true" keyProperty="boardId">
    INSERT INTO board (author_id, category, title, content)
    VALUES (#{authorId}, #{category}, #{title}, #{content})
</insert>
```

### 6-3. UPDATE

`<set>` + `<if>`로 null이 아닌 필드만 선택적으로 업데이트하고, 항상 `updated_at = CURRENT_TIMESTAMP`를 포함한다.

```xml
<!-- 게시글 부분 수정: null이 아닌 필드만 업데이트 -->
<update id="update">
    UPDATE board
    <set>
        <if test="request.title != null">title = #{request.title},</if>
        <if test="request.content != null">content = #{request.content},</if>
        updated_at = CURRENT_TIMESTAMP
    </set>
    WHERE board_id = #{boardId} AND is_deleted = FALSE
</update>
```

### 6-4. UPSERT (PostgreSQL)

중복 키 충돌 시 `ON CONFLICT ... DO UPDATE`를 사용한다.

```xml
<!-- 리프레시 토큰 저장: username 충돌 시 기존 토큰을 덮어씀 -->
<insert id="save" parameterType="com.aenggukland.letspt.security.RefreshToken">
    INSERT INTO refresh_token (username, token, expires_at)
    VALUES (#{username}, #{token}, #{expiresAt})
    ON CONFLICT (username)
        DO UPDATE SET token = EXCLUDED.token, expires_at = EXCLUDED.expires_at
</insert>
```

---

## 7. 데이터베이스 규칙

### 7-1. 컬럼 네이밍

- 모든 컬럼명: `snake_case`
- PK: `테이블명_id` (예: `board_id`, `member_id`)

### 7-2. 소프트 삭제 (필수)

물리 삭제 금지. 모든 삭제는 플래그 업데이트로 처리한다.

```sql
-- 삭제 처리: 물리 삭제 대신 플래그와 타임스탬프 설정
UPDATE board
SET is_deleted = TRUE, deleted_at = CURRENT_TIMESTAMP
WHERE board_id = #{boardId};

-- 조회 시 반드시 삭제 제외 조건 포함
SELECT * FROM board WHERE board_id = #{boardId} AND is_deleted = FALSE;
```

### 7-3. 감사 컬럼 (Audit Columns)

모든 테이블에 아래 컬럼을 포함한다.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `created_at` | `TIMESTAMP` | 레코드 생성 시각 (DB 자동) |
| `updated_at` | `TIMESTAMP` | 마지막 수정 시각 |
| `is_deleted` | `BOOLEAN` | 소프트 삭제 여부 (기본값 FALSE) |
| `deleted_at` | `TIMESTAMP` | 소프트 삭제 시각 (nullable) |

### 7-4. 데이터 타입

| Java 타입 | 용도 |
|-----------|------|
| `Long` | PK, FK (ID 계열) |
| `BigDecimal` | 신체 측정값 (키, 몸무게 등) |
| `LocalDateTime` | 날짜/시간 |
| `Boolean` | 플래그 (is_deleted 등) |
| `Integer` | 나이, 수량 등 범위 제한 숫자 |

---

## 8. DTO 규칙

### 8-1. Request DTO

- `@Getter` (Lombok)
- `@NotBlank`, `@Size`, `@Pattern` 등 Bean Validation으로 입력값 검증
- 검증 실패 메시지는 한국어로 작성

```java
// 게시글 생성 요청 DTO
// category는 고정값 세 가지만 허용하며, title·content는 필수 입력이다
@Getter
public class BoardCreateRequest {

    // 게시글 카테고리: LESSON, DIET, EXERCISE 중 하나
    @NotBlank
    @Pattern(regexp = "^(LESSON|DIET|EXERCISE)$", message = "올바른 카테고리를 입력해주세요.")
    private String category;

    // 게시글 제목: 최대 200자
    @NotBlank
    @Size(max = 200, message = "제목은 200자 이하로 입력해주세요.")
    private String title;

    // 게시글 본문
    @NotBlank
    private String content;
}
```

### 8-2. Response DTO

- `@Getter` (Lombok)
- 엔티티에서 직접 변환하는 정적 팩토리 메서드 또는 생성자 사용
- 비밀번호 등 민감 정보는 절대 포함하지 않는다

---

## 9. Enum 규칙

### 9-1. 단순 Enum

```java
// 게시글 카테고리: LESSON(레슨), DIET(식단), EXERCISE(운동)
public enum BoardCategory {
    LESSON,
    DIET,
    EXERCISE
}
```

### 9-2. 값을 가지는 Enum

DB에 숫자로 저장되는 역할 등은 `fromXxx()` 정적 팩토리 메서드를 제공한다.

```java
// 회원 역할: DB에는 role_id(Long)로 저장됨
// fromRoleId()로 숫자 → Enum 변환, 알 수 없는 값이면 예외를 던진다
public enum MemberRole {
    MEMBER(1L),
    TRAINER(2L),
    MASTER(3L);

    private final long roleId;

    MemberRole(long roleId) {
        this.roleId = roleId;
    }

    // role_id 숫자로 MemberRole Enum을 반환
    // 매핑되는 값이 없으면 IllegalArgumentException 발생
    public static MemberRole fromRoleId(long roleId) {
        for (MemberRole role : values()) {
            if (role.roleId == roleId) return role;
        }
        throw new IllegalArgumentException("알 수 없는 roleId: " + roleId);
    }
}
```
