CREATE TABLE customers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID UNIQUE REFERENCES users(id),
  name VARCHAR(100) NOT NULL,
  dob DATE,
  phone VARCHAR(15) NOT NULL,
  alternate_phone VARCHAR(15),
  email VARCHAR(150),
  address TEXT,
  pincode VARCHAR(10),
  city VARCHAR(80),
  state VARCHAR(80),
  aadhar_doc_url TEXT,
  pan_doc_url TEXT,
  photo_url TEXT,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_name ON customers(name);
CREATE TABLE customer_family_members (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
  name VARCHAR(100) NOT NULL,
  relation VARCHAR(20) NOT NULL,
  dob DATE,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

