-- 계약 요청, 제안, 최종 계약 endpoint seed입니다.
-- endpoint 접근 정책은 운영 DB의 end_points 테이블과 캐시 reload를 기준으로 적용됩니다.

INSERT INTO end_points (url, method, roles, description, created_at, updated_at)
VALUES
    ('/api/v1/contract-requests', 'POST', '["ADMIN","VENDOR","AGENCY"]', '화주 또는 대리점이 상대방에게 계약 요청을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests', 'GET', '["ADMIN","VENDOR","AGENCY"]', '로그인한 사용자가 요청자로 생성한 계약 요청 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/received', 'GET', '["ADMIN","VENDOR","AGENCY"]', '로그인한 사용자가 승인자로 지정된 계약 요청 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/open', 'GET', '["ADMIN","AGENCY"]', '대리점이 제안할 수 있는 OPEN 상태 계약 요청 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}', 'GET', '["ADMIN","VENDOR","AGENCY"]', '로그인한 사용자가 참여자인 계약 요청 상세 정보를 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}', 'PUT', '["ADMIN","VENDOR","AGENCY"]', '로그인한 요청자가 계약 요청 정보를 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}/cancel', 'POST', '["ADMIN","VENDOR","AGENCY"]', '로그인한 요청자가 계약 요청을 취소합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}/accept', 'POST', '["ADMIN","VENDOR","AGENCY"]', '승인자로 지정된 사용자가 계약 요청을 수락하고 최종 계약을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}/reject', 'POST', '["ADMIN","VENDOR","AGENCY"]', '승인자로 지정된 사용자가 계약 요청을 거절합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}/proposals', 'POST', '["ADMIN","AGENCY"]', '대리점이 계약 요청에 단가와 서비스 조건을 제안합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}/proposals', 'GET', '["ADMIN","VENDOR"]', '화주가 자신의 계약 요청에 제출된 제안 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점의 제안 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}', 'PUT', '["ADMIN","AGENCY"]', '대리점이 자신이 제출한 제안의 단가와 조건을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}/withdraw', 'POST', '["ADMIN","AGENCY"]', '대리점이 자신이 제출한 제안을 철회합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}/accept', 'POST', '["ADMIN","VENDOR"]', '화주가 대리점 제안을 선택해 최종 계약을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}/negotiations', 'GET', '["ADMIN","VENDOR","AGENCY"]', '화주 또는 대리점이 제안의 가격 조율 이벤트 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}/negotiations/price-offers', 'POST', '["ADMIN","VENDOR","AGENCY"]', '화주 또는 대리점이 제안 단가 조율 이벤트를 등록합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}/negotiations/{eventId}/accept', 'POST', '["ADMIN","VENDOR","AGENCY"]', '상대방이 제안한 가격 조율 이벤트를 수락합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}/negotiations/{eventId}/reject', 'POST', '["ADMIN","VENDOR","AGENCY"]', '상대방이 제안한 가격 조율 이벤트를 거절합니다.', NOW(6), NOW(6)),
    ('/api/v1/contracts/vendor/me', 'GET', '["ADMIN","VENDOR"]', '로그인한 화주의 최종 계약 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contracts/agency/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점의 최종 계약 목록을 조회합니다.', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);
