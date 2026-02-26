# LetSPT

PT 관리 서비스 — 회원/트레이너/마스터 3개 권한으로 구성된 PT(Personal Training) 관리 플랫폼

**회원**: 회원가입·로그인·탈퇴, 일정관리, 트레이너 채팅(Kafka), AI 챗봇, 식단·자세·수업 게시판, 당일 수업 알림, 트레이너 정보 열람
**트레이너(관리자)**: 회원/일정/식단 관리, 식단 이모지 피드백(따봉·X·체크 → 회원에게 표시), 회원 등록, 채팅(Kafka), 게시판 관리, 내 정보 작성·수정
**마스터**: 트레이너 회원가입 시 권한 부여

## 기술 스택

- **Backend**: Spring Boot 4.0.1, Java 17, MyBatis, Spring Security + JWT, Thymeleaf, Lombok
- **DB**: PostgreSQL 16
- **메시징**: Apache Kafka (트레이너-회원 채팅)
- **AI**: 외부 AI API (챗봇)
- **빌드**: Gradle (IntelliJ IDEA)
- **인프라**: Docker, AWS EC2, Jenkins CI/CD, Git

## 빌드 및 실행

```bash
./gradlew bootRun        # 로컬 실행
./gradlew test           # 테스트
```

## 환경 변수 (.env)

`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`

## 패키지 구조

`com.aenggukland.letspt` 하위에 `config/`, `member/`, `security/` 도메인 중심으로 구성

