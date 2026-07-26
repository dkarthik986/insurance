CREATE TABLE audit_events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  actor_user_id UUID REFERENCES users(id),
  actor_role VARCHAR(20),
  action VARCHAR(60) NOT NULL,
  entity_type VARCHAR(60),
  entity_id UUID,
  description TEXT NOT NULL,
  before_data JSONB,
  after_data JSONB,
  ip_address VARCHAR(64),
  user_agent TEXT,
  trace_id VARCHAR(80),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_audit_events_actor ON audit_events(actor_user_id, created_at DESC);
CREATE INDEX idx_audit_events_entity ON audit_events(entity_type, entity_id, created_at DESC);

CREATE TABLE document_metadata (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_customer_id UUID REFERENCES customers(id),
  policy_id UUID REFERENCES policies(id),
  vehicle_id UUID REFERENCES vehicles(id),
  document_type VARCHAR(40) NOT NULL,
  original_filename VARCHAR(255) NOT NULL,
  content_type VARCHAR(120) NOT NULL,
  size_bytes BIGINT NOT NULL,
  storage_url TEXT NOT NULL,
  checksum VARCHAR(128),
  uploaded_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_document_metadata_owner ON document_metadata(owner_customer_id, created_at DESC);
CREATE INDEX idx_document_metadata_policy ON document_metadata(policy_id, created_at DESC);

CREATE TABLE notification_attempts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  notification_id UUID NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
  attempt_number INTEGER NOT NULL,
  provider VARCHAR(30) NOT NULL,
  success BOOLEAN NOT NULL DEFAULT FALSE,
  provider_message TEXT,
  error_message TEXT,
  attempted_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_notification_attempts_notification ON notification_attempts(notification_id, attempted_at DESC);

