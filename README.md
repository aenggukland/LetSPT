# LetSPT

PT(Personal Training) 관리 플랫폼 — 회원·트레이너·마스터 3개 권한으로 구성된 헬스장 PT 관리 서비스

---

## 주요 기능

| 권한 | 기능 |
|------|------|
| **회원** | 회원가입·로그인·탈퇴, 수업 일정 요청/확인/취소, 트레이너 채팅, AI 챗봇, 식단·자세·수업 게시판, 당일 수업 FCM 알림 |
| **트레이너** | 회원/일정/식단 관리, 식단 이모지 피드백(따봉·X·체크), 회원 등록, 게시판 관리, 내 정보 작성·수정 |
| **마스터** | 트레이너 계정 권한 부여 |

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Backend | Spring Boot 4.0.1, Java 17, MyBatis, Lombok |
| 인증 | Spring Security + JWT (Access 30분 / Refresh 7일) |
| View | Thymeleaf |
| DB | PostgreSQL 16 |
| 메시징 | Apache Kafka (트레이너-회원 실시간 채팅), WebSocket |
| 알림 | Firebase Cloud Messaging (FCM) |
| 캐시 | Redis 7 (FCM 중복 알림 제거) |
| 빌드 | Gradle, Docker (멀티스테이지 빌드) |
| 인프라 | AWS EC2, Jenkins CI/CD |
| API 문서 | SpringDoc OpenAPI (Swagger UI) |

---

## 프로젝트 구조

```
src/main/java/com/aenggukland/letspt/
├── auth/           # 인증 페이지 컨트롤러
├── board/          # 게시판 (식단·자세·수업)
├── chat/           # Kafka 기반 채팅
├── comment/        # 게시글 댓글
├── config/         # Security, Kafka, Redis, Firebase, WebSocket 설정
├── fcm/            # FCM 토큰 관리 및 알림 발송
├── member/         # 회원 도메인 (CRUD, 권한)
├── schedule/       # 수업 일정 관리
├── scheduler/      # 스케줄러 (당일 수업 알림 등)
└── security/       # JWT Provider / Filter
```

---

## 환경 변수

`.env` 파일을 프로젝트 루트에 생성하세요.

```env
POSTGRES_DB=letspt
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
COOKIE_SECURE=false
UPLOAD_DIR=/path/to/uploads
FIREBASE_SERVICE_ACCOUNT_PATH=/path/to/firebase-service-account.json
```

---

## 실행 방법

### 로컬 실행 (Docker 필요)

```bash
# 인프라 컨테이너 실행 (PostgreSQL, Kafka, Redis)
docker-compose up -d postgres zookeeper kafka redis

# 애플리케이션 실행
./gradlew bootRun
```

### 전체 Docker 실행

```bash
docker-compose up -d
```

### 테스트

```bash
./gradlew test
```

---

## Docker 빌드 및 배포

```bash
# 이미지 빌드
docker build -t inkug/letspt-app:latest .

# 이미지 푸시
docker push inkug/letspt-app:latest
```

---

## API 문서

애플리케이션 실행 후 아래 주소에서 Swagger UI를 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui/index.html
```

---

## 아키텍처 개요

```
Client (Browser)
    │
    ├── HTTP/Thymeleaf ──→ Spring Boot App ──→ PostgreSQL
    │
    ├── WebSocket ────────→ Kafka ──────────→ Chat Consumer
    │
    └── FCM (Push) ←──── Firebase Admin SDK
                              ↑
                           Redis (중복 제거)
```

---

## 인증 흐름

1. 로그인 시 Access Token(30분) + Refresh Token(7일) 발급
2. Access Token 만료 시 Refresh Token으로 재발급
3. JWT는 HttpOnly 쿠키로 전달 (`COOKIE_SECURE` 환경 변수로 HTTPS 제어)
