-- 대리점, 배송기사, 배송기사 계약, 기사 일감 endpoint seed입니다.
-- endpoint 접근 정책은 운영 DB의 end_points 테이블과 캐시 reload를 기준으로 적용됩니다.

INSERT INTO end_points (url, method, roles, description, created_at, updated_at)
VALUES
    ('/api/v1/agencies', 'GET', '["PUBLIC"]', '화주는 계약 요청 대상 대리점을 조회하고, 배송기사는 회원가입 또는 프로필 등록 시 소속 대리점을 검색합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/{agencyId}', 'GET', '["PUBLIC"]', '화주와 배송기사가 대리점 대표자명, 연락처, 주소, 서비스 조건 등 상세 정보를 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'POST', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/agencies/me', 'PUT', '["ADMIN","AGENCY"]', '로그인한 대리점 사용자의 영업 거점 프로필을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'POST', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'GET', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/me', 'PUT', '["ADMIN","DRIVER"]', '로그인한 배송기사 사용자의 프로필을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/agency/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점에 소속된 배송기사 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/delivers/freelancers', 'GET', '["ADMIN","AGENCY"]', '대리점이 계약 요청할 수 있는 프리랜서 배송기사 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts', 'POST', '["ADMIN","AGENCY"]', '대리점이 소속 배송기사에게 담당 지역과 단가 조건을 제안합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/agency/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점이 생성한 배송기사 계약 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/driver/me', 'GET', '["ADMIN","DRIVER"]', '로그인한 배송기사가 자신에게 요청된 계약 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/{deliverContractId}', 'PUT', '["ADMIN","AGENCY"]', '대리점이 요청 상태의 배송기사 계약 조건을 수정합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/{deliverContractId}/accept', 'POST', '["ADMIN","DRIVER"]', '배송기사가 자신에게 요청된 배송기사 계약을 수락합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/{deliverContractId}/reject', 'POST', '["ADMIN","DRIVER"]', '배송기사가 자신에게 요청된 배송기사 계약을 거절합니다.', NOW(6), NOW(6)),
    ('/api/v1/deliver-contracts/{deliverContractId}/cancel', 'POST', '["ADMIN","AGENCY"]', '대리점이 요청 상태의 배송기사 계약을 취소합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works', 'POST', '["ADMIN","AGENCY"]', '대리점이 최종 계약 물량에 대한 기사 일감을 생성합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/agency/me', 'GET', '["ADMIN","AGENCY"]', '로그인한 대리점이 생성한 기사 일감 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/driver/me/open', 'GET', '["ADMIN","DRIVER"]', '로그인한 소속 배송기사가 신청 가능한 OPEN 일감 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/driver/me/assigned', 'GET', '["ADMIN","DRIVER"]', '로그인한 배송기사에게 확정 할당된 기사 일감 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/driver/me/applications', 'GET', '["ADMIN","DRIVER"]', '로그인한 배송기사가 신청한 기사 일감 신청 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/{driverWorkId}/applications', 'GET', '["ADMIN","AGENCY"]', '대리점이 특정 기사 일감의 신청자 목록을 조회합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/{driverWorkId}/applications', 'POST', '["ADMIN","DRIVER"]', '소속 배송기사가 OPEN 상태 기사 일감에 신청합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/{driverWorkId}/applications/me', 'DELETE', '["ADMIN","DRIVER"]', '소속 배송기사가 OPEN 상태 일감에 대한 신청을 철회합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/{driverWorkId}/applications/{applicationId}/select', 'POST', '["ADMIN","AGENCY"]', '대리점이 신청자 중 한 명을 선택해 기사 일감을 확정합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/{driverWorkId}/assign', 'POST', '["ADMIN","AGENCY"]', '대리점이 OPEN 상태 기사 일감을 소속 배송기사에게 직접 할당합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/{driverWorkId}/cancel', 'POST', '["ADMIN","AGENCY"]', '대리점이 완료 전 기사 일감을 취소합니다.', NOW(6), NOW(6)),
    ('/api/v1/driver-works/{driverWorkId}/complete', 'POST', '["ADMIN","AGENCY"]', '대리점이 할당된 기사 일감을 완료 처리합니다.', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    roles = VALUES(roles),
    description = VALUES(description),
    updated_at = VALUES(updated_at);
