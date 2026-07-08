CREATE TABLE IF NOT EXISTS notifications (
    id BINARY(16) NOT NULL COMMENT '알림 식별자',
    receiver_user_id BINARY(16) NOT NULL COMMENT '알림 수신 사용자 식별자',
    sender_user_id BINARY(16) NULL COMMENT '알림 발신 사용자 식별자',
    type VARCHAR(50) NOT NULL COMMENT '알림 유형',
    title VARCHAR(100) NOT NULL COMMENT '알림 제목',
    message VARCHAR(500) NOT NULL COMMENT '알림 메시지',
    link_url VARCHAR(255) NULL COMMENT '알림 클릭 이동 경로',
    reference_type VARCHAR(50) NULL COMMENT '알림 참조 대상 유형',
    reference_id BINARY(16) NULL COMMENT '알림 참조 대상 식별자',
    read_at DATETIME(6) NULL COMMENT '읽은 시각',
    created_at DATETIME(6) NOT NULL COMMENT '생성 시각',
    updated_at DATETIME(6) NOT NULL COMMENT '마지막 수정 시각',
    PRIMARY KEY (id),
    KEY idx_notifications_receiver_created_at (receiver_user_id, created_at),
    KEY idx_notifications_receiver_read_at_created_at (receiver_user_id, read_at, created_at),
    KEY idx_notifications_reference (reference_type, reference_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='알림';
