-- Local bootstrap account: agent@insuredesk.local / password
-- Override it immediately in every non-local environment.
INSERT INTO users (name, email, phone, password_hash, role, is_active)
VALUES (
  'Insurance Agent',
  'agent@insuredesk.local',
  '9999999999',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'DEALER',
  TRUE
)
ON CONFLICT (email) DO NOTHING;

