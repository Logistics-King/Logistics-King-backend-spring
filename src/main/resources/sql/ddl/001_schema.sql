CREATE TABLE IF NOT EXISTS users (
    id BINARY(16) NOT NULL,
    login_id VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    encoded_password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_login_id (login_id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
