-- DML seed/reference data scripts live here.
-- Keep runtime business data changes in application code, not seed scripts.

INSERT INTO end_points (url, role, description, created_at, updated_at)
VALUES ('/api/v1/vendors/**', 'VENDOR', '화주 프로필 및 배송 품목 API', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    updated_at = VALUES(updated_at);
