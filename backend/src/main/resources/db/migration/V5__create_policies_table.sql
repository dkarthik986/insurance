CREATE TABLE policies (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  policy_number VARCHAR(80) UNIQUE NOT NULL,
  customer_id UUID NOT NULL REFERENCES customers(id),
  vehicle_id UUID REFERENCES vehicles(id),
  parent_policy_id UUID REFERENCES policies(id),
  policy_type VARCHAR(20) NOT NULL,
  company VARCHAR(30) NOT NULL,
  plan_name VARCHAR(200) NOT NULL,
  sum_insured NUMERIC(15,2),
  idv NUMERIC(12,2),
  premium_amount NUMERIC(12,2) NOT NULL,
  gst_amount NUMERIC(10,2),
  total_premium NUMERIC(12,2),
  payment_frequency VARCHAR(20) NOT NULL DEFAULT 'YEARLY',
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  maturity_date DATE,
  policy_term_years INT,
  premium_paying_term INT,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  policy_doc_url TEXT,
  commission_rate NUMERIC(5,2),
  commission_amount NUMERIC(12,2),
  commission_received BOOLEAN NOT NULL DEFAULT FALSE,
  commission_received_date DATE,
  ncb_percentage INT NOT NULL DEFAULT 0,
  zero_dep BOOLEAN NOT NULL DEFAULT FALSE,
  engine_protect BOOLEAN NOT NULL DEFAULT FALSE,
  ncb_protect BOOLEAN NOT NULL DEFAULT FALSE,
  roadside_assistance BOOLEAN NOT NULL DEFAULT FALSE,
  long_term BOOLEAN NOT NULL DEFAULT FALSE,
  riders TEXT,
  family_floater BOOLEAN NOT NULL DEFAULT FALSE,
  members_covered TEXT,
  pre_existing_disease BOOLEAN NOT NULL DEFAULT FALSE,
  waiting_period_days INT,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_policies_customer ON policies(customer_id);
CREATE INDEX idx_policies_end_date ON policies(end_date);
CREATE INDEX idx_policies_status ON policies(status);
CREATE INDEX idx_policies_company ON policies(company);
CREATE INDEX idx_policies_type ON policies(policy_type);
CREATE INDEX idx_policies_not_deleted ON policies(is_deleted) WHERE is_deleted = FALSE;

