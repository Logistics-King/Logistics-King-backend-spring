CREATE TABLE IF NOT EXISTS users (
    id BINARY(16) NOT NULL COMMENT '사용자 식별자',
    login_id VARCHAR(50) NOT NULL COMMENT '로그인 ID',
    email VARCHAR(255) NOT NULL COMMENT '이메일',
    encoded_password VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호',
    name VARCHAR(50) NOT NULL COMMENT '사용자 이름',
    role VARCHAR(30) NOT NULL COMMENT '사용자 역할',
    created_at DATETIME(6) NOT NULL COMMENT '생성 시각',
    updated_at DATETIME(6) NOT NULL COMMENT '마지막 수정 시각',
    deleted_at DATETIME(6) NULL COMMENT '삭제 시각',
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_login_id (login_id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자';

CREATE TABLE IF NOT EXISTS end_points (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '엔드포인트 권한 식별자',
    url VARCHAR(255) NOT NULL COMMENT 'API URL 패턴',
    method VARCHAR(10) NOT NULL COMMENT 'HTTP method',
    roles JSON NOT NULL COMMENT '접근 허용 역할 목록',
    description VARCHAR(255) NULL COMMENT '엔드포인트 설명',
    created_at DATETIME(6) NOT NULL COMMENT '생성 시각',
    updated_at DATETIME(6) NOT NULL COMMENT '마지막 수정 시각',
    PRIMARY KEY (id),
    UNIQUE KEY uk_end_points_url_method (url, method)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='엔드포인트 접근 권한';

CREATE TABLE IF NOT EXISTS account_recovery_tokens (
    id BINARY(16) NOT NULL COMMENT '계정 복구 토큰 식별자',
    user_id BINARY(16) NOT NULL COMMENT '복구 대상 사용자 식별자',
    purpose VARCHAR(30) NOT NULL COMMENT '토큰 목적',
    token_hash VARCHAR(64) NOT NULL COMMENT '복구 토큰 해시',
    expires_at DATETIME(6) NOT NULL COMMENT '만료 시각',
    used_at DATETIME(6) NULL COMMENT '사용 시각',
    created_at DATETIME(6) NOT NULL COMMENT '생성 시각',
    updated_at DATETIME(6) NOT NULL COMMENT '마지막 수정 시각',
    PRIMARY KEY (id),
    UNIQUE KEY uk_account_recovery_tokens_token_hash (token_hash),
    KEY idx_account_recovery_tokens_user_purpose (user_id, purpose),
    KEY idx_account_recovery_tokens_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='계정 복구 토큰';
