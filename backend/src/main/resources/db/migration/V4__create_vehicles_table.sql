CREATE TABLE vehicles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id UUID NOT NULL REFERENCES customers(id),
  reg_number VARCHAR(20) UNIQUE NOT NULL,
  make VARCHAR(60),
  model VARCHAR(80),
  year INT,
  fuel_type VARCHAR(20),
  vehicle_type VARCHAR(20) NOT NULL,
  chassis_number VARCHAR(50),
  engine_number VARCHAR(50),
  rc_doc_url TEXT,
  puc_doc_url TEXT,
  puc_expiry_date DATE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_vehicles_reg ON vehicles(reg_number);
CREATE INDEX idx_vehicles_customer ON vehicles(customer_id);

