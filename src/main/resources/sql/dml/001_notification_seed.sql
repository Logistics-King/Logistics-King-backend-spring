-- 알림 endpoint seed입니다.
-- endpoint 접근 정책은 운영 DB의 end_points 테이블과 캐시 reload를 기준으로 적용됩니다.

INSERT INTO end_points (url, method, roles, description, created_at, updated_at)
VALUES
    ('/api/v1/notifications/me', 'GET', '["ADMIN","VENDOR","AGENCY","DRIVER"]', '로그인한 사용자의 최근 30일 알림 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/notifications/stream', 'GET', '["ADMIN","VENDOR","AGENCY","DRIVER"]', '로그인한 사용자의 새 알림을 SSE 스트림으로 구독합니다.', NOW(6), NOW(6)),
    ('/api/v1/notifications/me/unread-count', 'GET', '["ADMIN","VENDOR","AGENCY","DRIVER"]', '로그인한 사용자의 최근 30일 미확인 알림 수를 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/notifications/{notificationId}/read', 'PUT', '["ADMIN","VENDOR","AGENCY","DRIVER"]', '로그인한 사용자의 특정 알림을 읽음 처리합니다.', NOW(6), NOW(6)),
    ('/api/v1/notifications/me/read-all', 'PUT', '["ADMIN","VENDOR","AGENCY","DRIVER"]', '로그인한 사용자의 읽지 않은 알림을 모두 읽음 처리합니다.', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);
