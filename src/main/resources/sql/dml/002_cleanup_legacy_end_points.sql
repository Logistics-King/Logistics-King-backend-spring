-- Cleanup script for the old end_points schema that used URL-only rows.
-- Run manually after adding end_points.method if the local database still has legacy rows.

DELETE FROM end_points
WHERE method IS NULL
   OR method = '';
