-- Soft delete columns for profile-style domains.
-- Contract-related tables keep status-based lifecycle management.

ALTER TABLE users
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE vendors
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE products
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE agencies
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE delivers
    ADD COLUMN deleted_at DATETIME(6) NULL;
