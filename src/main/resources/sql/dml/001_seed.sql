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
    ('/api/v1/contract-requests', 'POST', '["ADMIN","VENDOR"]', '로그인한 화주가 대리점 제안을 받기 위한 계약 요청을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests', 'GET', '["ADMIN","VENDOR"]', '로그인한 화주의 계약 요청 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/open', 'GET', '["ADMIN","AGENCY"]', '대리점이 제안할 수 있는 OPEN 상태 계약 요청 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}', 'GET', '["ADMIN","VENDOR"]', '로그인한 화주의 계약 요청 상세 정보를 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}', 'PUT', '["ADMIN","VENDOR"]', '로그인한 화주의 계약 요청 정보를 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}/cancel', 'POST', '["ADMIN","VENDOR"]', '로그인한 화주의 계약 요청을 취소합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}/proposals', 'POST', '["ADMIN","AGENCY"]', '대리점이 계약 요청에 단가와 서비스 조건을 제안합니다.', NOW(6), NOW(6)),
    ('/api/v1/contract-requests/{contractRequestId}/proposals', 'GET', '["ADMIN","VENDOR"]', '화주가 자신의 계약 요청에 제출된 제안 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점의 제안 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}', 'PUT', '["ADMIN","AGENCY"]', '대리점이 자신이 제출한 제안의 단가와 조건을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}/withdraw', 'POST', '["ADMIN","AGENCY"]', '대리점이 자신이 제출한 제안을 철회합니다.', NOW(6), NOW(6)),
    ('/api/v1/proposals/{proposalId}/accept', 'POST', '["ADMIN","VENDOR"]', '화주가 대리점 제안을 선택해 최종 계약을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/contracts/vendor/me', 'GET', '["ADMIN","VENDOR"]', '로그인한 화주의 최종 계약 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/contracts/agency/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점의 최종 계약 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'POST', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'PUT', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'POST', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'GET', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'PUT', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts', 'POST', '["ADMIN","AGENCY"]', '대리점이 소속 배송기사에게 담당 지역과 단가 조건을 제안합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/agency/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점이 생성한 배송기사 계약 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/driver/me', 'GET', '["ADMIN","DRIVER"]', '로그인한 배송기사가 자신에게 요청된 계약 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/{deliverContractId}', 'PUT', '["ADMIN","AGENCY"]', '대리점이 요청 상태의 배송기사 계약 조건을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/{deliverContractId}/accept', 'POST', '["ADMIN","DRIVER"]', '배송기사가 자신에게 요청된 배송기사 계약을 수락합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/{deliverContractId}/reject', 'POST', '["ADMIN","DRIVER"]', '배송기사가 자신에게 요청된 배송기사 계약을 거절합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/{deliverContractId}/cancel', 'POST', '["ADMIN","AGENCY"]', '대리점이 요청 상태의 배송기사 계약을 취소합니다.', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);
