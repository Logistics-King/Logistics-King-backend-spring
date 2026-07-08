-- 인증/권한 도메인 보정 DML입니다.
-- method 없이 URL만 저장하던 예전 end_points row가 로컬 DB에 남아 있을 때 수동 실행합니다.

DELETE FROM end_points
WHERE method IS NULL
   OR method = '';
