-- 계약 도메인 보정 DML입니다.
-- 제안 단가 조율 컬럼이 추가되기 전 생성된 로컬 DB에 기본값을 보정합니다.

UPDATE proposals
SET next_sequence = 1
WHERE next_sequence <= 0;

UPDATE proposals
SET initial_unit_price = unit_price
WHERE initial_unit_price <= 0;
