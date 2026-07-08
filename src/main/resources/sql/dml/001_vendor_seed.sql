-- 화주와 배송 품목 endpoint seed입니다.
-- endpoint 접근 정책은 운영 DB의 end_points 테이블과 캐시 reload를 기준으로 적용됩니다.

INSERT INTO end_points (url, method, roles, description, created_at, updated_at)
VALUES
    ('/api/v1/vendors/me', 'POST', '["ADMIN","VENDOR"]', '로그인한 화주 사용자의 사업 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me', 'GET', '["ADMIN","VENDOR"]', '로그인한 화주 사용자의 사업 프로필을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me', 'PUT', '["ADMIN","VENDOR"]', '로그인한 화주 사용자의 사업 프로필을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me/products', 'POST', '["ADMIN","VENDOR"]', '계약 요청과 단가 산정에 사용할 배송 품목 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me/products', 'GET', '["ADMIN","VENDOR"]', '로그인한 화주의 배송 품목 프로필 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/vendors/me/products/{productId}', 'PUT', '["ADMIN","VENDOR"]', '로그인한 화주의 배송 품목 프로필을 수정합니다.', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);
