CREATE TABLE claims (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  policy_id UUID NOT NULL REFERENCES policies(id),
  claim_number VARCHAR(80),
  claim_date DATE NOT NULL,
  claim_type VARCHAR(150),
  claim_amount NUMERIC(12,2),
  status VARCHAR(30) NOT NULL DEFAULT 'FILED',
  settled_amount NUMERIC(12,2),
  settlement_date DATE,
  doc_url TEXT,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_claims_policy ON claims(policy_id);

