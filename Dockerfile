# =========================
# 1️⃣ 빌드 스테이지 (Jar 생성)
# =========================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Gradle 관련 파일 먼저 복사 (캐시 최적화)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한
RUN chmod +x gradlew

# 의존성 미리 다운로드
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# Jar 빌드
RUN ./gradlew clean build -x test --no-daemon


# =========================
# 2️⃣ 실행 스테이지 (가벼운 이미지)
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# 빌드 스테이지에서 jar만 가져오기
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트
EXPOSE 8080

# 시간대 설정
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
 && echo 'Asia/Seoul' > /etc/timezone

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]