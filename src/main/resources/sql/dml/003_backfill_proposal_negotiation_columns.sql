-- Backfill proposal negotiation columns for local databases created before proposal negotiation was added.

UPDATE proposals
SET next_sequence = 1
WHERE next_sequence <= 0;

UPDATE proposals
SET initial_unit_price = unit_price
WHERE initial_unit_price <= 0;
