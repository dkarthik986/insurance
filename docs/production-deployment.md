# Production deployment runbook

## Backend (Render or container)

Set `SERVER_PORT`, `DB_URL`, `DB_USER`, `DB_PASS`, `JWT_SECRET`, `APP_ALLOWED_ORIGINS`, `AUTH_COOKIE_SECURE=true`, `AUTH_COOKIE_SAME_SITE=None`, and the optional mail, WhatsApp and Cloudinary variables. Use a managed PostgreSQL database and keep `JPA_DDL_AUTO=validate`.

Build and start:

```powershell
cd backend
.\mvnw.cmd -q -DskipTests package
java -jar target\insurance-agent-backend-1.0.0.jar
```

Health check: `GET /actuator/health`.

## Frontend (Vercel)

Set `VITE_API_BASE_URL` to the deployed API, `VITE_USE_REMOTE_MFES=true` only after every remote entry is deployed, and configure each `VITE_MFE_*_URL`. Keep `VITE_ENABLE_DEMO_DATA=false`.

## Rollback and recovery

Deploy immutable backend/frontend artifacts, retain the previous release, and roll back the application before changing database schema. Take automated PostgreSQL backups and test a restore before launch. Flyway migrations are forward-only; never edit an applied migration.
