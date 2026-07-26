CREATE TABLE premium_schedule (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  policy_id UUID NOT NULL REFERENCES policies(id) ON DELETE CASCADE,
  due_date DATE NOT NULL,
  amount NUMERIC(12,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  paid_date DATE,
  receipt_number VARCHAR(80),
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_premium_policy ON premium_schedule(policy_id);
CREATE INDEX idx_premium_due_date ON premium_schedule(due_date);
CREATE INDEX idx_premium_status ON premium_schedule(status);

