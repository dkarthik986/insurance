CREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  policy_id UUID REFERENCES policies(id),
  customer_id UUID REFERENCES customers(id),
  type VARCHAR(40) NOT NULL,
  channel VARCHAR(20) NOT NULL,
  message TEXT NOT NULL,
  is_sent BOOLEAN NOT NULL DEFAULT FALSE,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  sent_at TIMESTAMP,
  scheduled_for DATE,
  error_message TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_notif_customer ON notifications(customer_id);
CREATE INDEX idx_notif_is_sent ON notifications(is_sent);

