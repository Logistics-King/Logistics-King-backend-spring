-- DML seed/reference data scripts live here.
-- Keep runtime business data changes in application code, not seed scripts.

INSERT INTO end_points (url, method, roles, description, created_at, updated_at)
VALUES
    ('/api/v1/vendors/me', 'POST', '["ADMIN","VENDOR"]', '로그인한 화주 사용자의 사업 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me', 'GET', '["ADMIN","VENDOR"]', '로그인한 화주 사용자의 사업 프로필을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me', 'PUT', '["ADMIN","VENDOR"]', '로그인한 화주 사용자의 사업 프로필을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me/products', 'POST', '["ADMIN","VENDOR"]', '계약 요청과 단가 산정에 사용할 배송 품목 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me/products', 'GET', '["ADMIN","VENDOR"]', '로그인한 화주의 배송 품목 프로필 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me/products/{productId}', 'PUT', '["ADMIN","VENDOR"]', '로그인한 화주의 배송 품목 프로필을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'POST', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'PUT', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'POST', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'GET', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'PUT', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 수정합니다.', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);
