CREATE TABLE IF NOT EXISTS deliver_contracts (
    id BINARY(16) NOT NULL,
    agency_id BINARY(16) NOT NULL,
    deliver_id BINARY(16) NOT NULL,
    service_region VARCHAR(100) NOT NULL,
    expected_monthly_volume INT NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    memo VARCHAR(255) NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_deliver_contracts_agency_id (agency_id),
    KEY idx_deliver_contracts_deliver_id (deliver_id),
    KEY idx_deliver_contracts_agency_deliver_status (agency_id, deliver_id, status),
    KEY idx_deliver_contracts_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
