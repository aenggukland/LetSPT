-- =========================
-- 1. ROLE (권한)
-- =========================
CREATE TABLE IF NOT EXISTS role (
    role_id     BIGSERIAL PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 기본 권한 데이터
INSERT INTO role (role_name, description)
VALUES
    ('MEMBER',  '일반 회원'),
    ('TRAINER', '트레이너'),
    ('MASTER',  '마스터 관리자')
ON CONFLICT (role_name) DO NOTHING;

-- =========================
-- 2. ERROR_LOG (에러 로그)
-- =========================
CREATE TABLE IF NOT EXISTS error_log (
    log_id      BIGSERIAL PRIMARY KEY,
    error_code  VARCHAR(100),
    method      VARCHAR(10),
    url         VARCHAR(255),
    username    VARCHAR(50),
    message     TEXT,
    stack_trace TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 3. IMAGE (파일/이미지)
-- =========================
CREATE TABLE IF NOT EXISTS image (
    image_id   BIGSERIAL PRIMARY KEY,
    file_name  VARCHAR(255) NOT NULL,
    file_path  VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 4. MEMBER (회원)
-- =========================
CREATE TABLE IF NOT EXISTS member (
    member_id           BIGSERIAL PRIMARY KEY,
    role_id             BIGINT NOT NULL DEFAULT 1,
    username            VARCHAR(50) NOT NULL UNIQUE,
    name                VARCHAR(50),
    gender              VARCHAR(10),
    age                 INT,
    height              NUMERIC(5,2),
    weight              NUMERIC(5,2),
    body_fat_percentage NUMERIC(5,2),
    target_weight       NUMERIC(5,2),
    fitness_goal        VARCHAR(255),
    phone_number        VARCHAR(20),
    is_deleted          BOOLEAN DEFAULT FALSE,
    password            VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    deleted_at          TIMESTAMP,
    last_login_at       TIMESTAMP,
    CONSTRAINT fk_member_role FOREIGN KEY (role_id) REFERENCES role(role_id)
);

ALTER TABLE member
    ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500);

-- =========================
-- 5. REFRESH_TOKEN (리프레시 토큰)
-- =========================
CREATE TABLE IF NOT EXISTS refresh_token (
    token_id   BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_member FOREIGN KEY (username) REFERENCES member(username)
);

-- =========================
-- 6. BOARD (게시판 통합)
-- =========================
CREATE TABLE IF NOT EXISTS board (
    board_id   BIGSERIAL PRIMARY KEY,
    image_id   BIGINT,
    author_id  BIGINT NOT NULL,
    member_id  BIGINT,
    category   VARCHAR(20) NOT NULL CHECK (category IN ('LESSON', 'DIET', 'EXERCISE')),
    title      VARCHAR(200) NOT NULL,
    content    TEXT,
    is_deleted BOOLEAN   DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_board_image  FOREIGN KEY (image_id)  REFERENCES image(image_id),
    CONSTRAINT fk_board_author FOREIGN KEY (author_id) REFERENCES member(member_id),
    CONSTRAINT fk_board_member FOREIGN KEY (member_id) REFERENCES member(member_id)
);


-- =========================
-- 7. SCHEDULE (일정)
-- =========================
CREATE TABLE schedule (
      schedule_id    BIGSERIAL PRIMARY KEY,
      trainer_id     BIGINT NOT NULL,
      member_id      BIGINT NOT NULL,
      start_datetime TIMESTAMP NOT NULL,
      end_datetime   TIMESTAMP NOT NULL,
      class_content  TEXT,
      state          VARCHAR(20) NOT NULL CHECK (state IN ('RESERVATION', 'COMPLETE', 'CANCEL', 'MEMBER_CANCEL','FINISH')),
      memo           TEXT,
      created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT fk_schedule_trainer FOREIGN KEY (trainer_id) REFERENCES member(member_id),
      CONSTRAINT fk_schedule_member  FOREIGN KEY (member_id)  REFERENCES member(member_id),
      CONSTRAINT uq_schedule_trainer_time UNIQUE (trainer_id, start_datetime)
);