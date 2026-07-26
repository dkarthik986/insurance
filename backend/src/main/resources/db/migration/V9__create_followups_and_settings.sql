CREATE TABLE follow_ups (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id UUID NOT NULL REFERENCES customers(id),
  policy_id UUID REFERENCES policies(id),
  note TEXT NOT NULL,
  follow_up_date DATE NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  lead_status VARCHAR(10) NOT NULL DEFAULT 'WARM',
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_followup_customer ON follow_ups(customer_id);
CREATE INDEX idx_followup_date ON follow_ups(follow_up_date);
CREATE TABLE reminder_settings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  reminder_days INTEGER[] NOT NULL DEFAULT ARRAY[30,15,7,1],
  email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  whatsapp_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
INSERT INTO reminder_settings DEFAULT VALUES;

