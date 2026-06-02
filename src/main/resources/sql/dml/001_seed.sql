-- DML seed/reference data scripts live here.
-- Keep runtime business data changes in application code, not seed scripts.

INSERT INTO end_points (url, roles, description, created_at, updated_at)
VALUES ('/api/v1/vendors/**', '["ADMIN","VENDOR"]', '화주 프로필 및 배송 품목 API', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);

INSERT INTO end_points (url, roles, description, created_at, updated_at)
VALUES ('/api/v1/agencies/**', '["ADMIN","AGENCY"]', '대리점 프로필 API', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);

INSERT INTO end_points (url, roles, description, created_at, updated_at)
VALUES ('/api/v1/delivers/**', '["ADMIN","DRIVER"]', '배송기사 프로필 API', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);
