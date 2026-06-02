-- DML seed/reference data scripts live here.
-- Keep runtime business data changes in application code, not seed scripts.

INSERT INTO end_points (url, description, created_at, updated_at)
VALUES ('/api/v1/vendors/**', '화주 프로필 및 배송 품목 API', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    updated_at = VALUES(updated_at);

INSERT INTO end_point_roles (end_point_id, role)
SELECT id, 'VENDOR'
FROM end_points
WHERE url = '/api/v1/vendors/**'
ON DUPLICATE KEY UPDATE
    role = VALUES(role);

INSERT INTO end_points (url, description, created_at, updated_at)
VALUES ('/api/v1/agencies/**', '대리점 프로필 API', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    updated_at = VALUES(updated_at);

INSERT INTO end_point_roles (end_point_id, role)
SELECT id, 'AGENCY'
FROM end_points
WHERE url = '/api/v1/agencies/**'
ON DUPLICATE KEY UPDATE
    role = VALUES(role);
