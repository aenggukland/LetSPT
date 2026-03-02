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
-- 2. REFRESH_TOKEN (리프레시 토큰)
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
-- 3. MEMBER (회원)
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
