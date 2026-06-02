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

CREATE TABLE IF NOT EXISTS end_points (
    url VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (url, role),
    UNIQUE KEY uk_end_points_url_role (url, role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vendors (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    business_name VARCHAR(100) NOT NULL,
    business_registration_number VARCHAR(30) NULL,
    representative_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    postal_code VARCHAR(20) NULL,
    address VARCHAR(255) NOT NULL,
    address_detail VARCHAR(255) NULL,
    main_region VARCHAR(100) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_vendors_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vendor_products (
    id BINARY(16) NOT NULL,
    vendor_id BINARY(16) NOT NULL,
    category VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    average_price DECIMAL(15, 2) NULL,
    average_weight_gram INT NULL,
    box_size VARCHAR(30) NULL,
    fragile BOOLEAN NOT NULL,
    liquid BOOLEAN NOT NULL,
    fresh_food BOOLEAN NOT NULL,
    requires_cold_chain BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_vendor_products_vendor_id (vendor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS agencies (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    carrier VARCHAR(30) NOT NULL,
    agency_name VARCHAR(100) NOT NULL,
    business_registration_number VARCHAR(30) NULL,
    representative_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    postal_code VARCHAR(20) NULL,
    address VARCHAR(255) NOT NULL,
    address_detail VARCHAR(255) NULL,
    main_region VARCHAR(100) NOT NULL,
    service_regions VARCHAR(500) NOT NULL,
    weekday_pickup_start_time VARCHAR(10) NULL,
    weekday_pickup_end_time VARCHAR(10) NULL,
    saturday_pickup_available BOOLEAN NOT NULL,
    saturday_delivery_available BOOLEAN NOT NULL,
    return_available BOOLEAN NOT NULL,
    cold_chain_available BOOLEAN NOT NULL,
    max_monthly_volume INT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_agencies_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
