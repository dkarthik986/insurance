-- Preserve V10's checksum because it may already be applied, then neutralize its
-- predictable account with a forward-only migration.
UPDATE users
SET is_active = FALSE,
    updated_at = now()
WHERE lower(email) = 'agent@insuredesk.local'
  AND password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';
