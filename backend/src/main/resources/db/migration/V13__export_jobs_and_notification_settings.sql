CREATE TABLE IF NOT EXISTS export_jobs (
    id UUID PRIMARY KEY,
    requested_by UUID NOT NULL REFERENCES users(id),
    export_type VARCHAR(60) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'QUEUED',
    file_url VARCHAR(1000),
    error_message VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_export_jobs_requested_by_created
    ON export_jobs(requested_by, created_at DESC);

CREATE TABLE IF NOT EXISTS notification_settings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    email_enabled BOOLEAN NOT NULL DEFAULT true,
    whatsapp_enabled BOOLEAN NOT NULL DEFAULT false,
    reminder_days INTEGER NOT NULL DEFAULT 30,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
