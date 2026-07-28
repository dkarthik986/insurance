# Production launch checklist

## 1. Local PostgreSQL

Set the password for the PostgreSQL 18 `postgres` role, then run:

```bat
set DB_PASS=your-password
backend\check-database.cmd
backend\start-backend.cmd
```

Spring Boot runs the ten Flyway migrations automatically. Confirm the startup
log reports the migrations as successful before using the application.

## 2. Required backend secrets

Configure these in Render or the backend process environment:

- `DB_URL`, `DB_USER`, `DB_PASS`
- `JWT_SECRET` (32+ random characters)
- `APP_ALLOWED_ORIGINS`
- `GMAIL_USER`, `GMAIL_APP_PASS`
- `WA_TOKEN`, `WA_PHONE_ID`
- `CLOUD_NAME`, `CLOUD_KEY`, `CLOUD_SECRET`

Keep `JPA_DDL_AUTO=validate` in production. Never commit `.env` files.

## 3. Frontend deployment

Deploy each `frontend/*` application as its own Vercel project. Set the
production values from `frontend/.env.production.example`; the shell uses
`VITE_USE_REMOTE_MFES=true` to load each `remoteEntry.js`.

The backend CORS value must include the deployed shell and remote origins.

## 4. Validation

- `GET /actuator/health` returns `UP`
- `POST /api/v1/auth/login` returns a JWT
- dealer routes reject customer tokens
- portal routes only return the authenticated customer's records
- policy creation triggers notification records
- LIC policies create premium schedule rows
- Excel and PDF exports download successfully
- Vercel shell loads all seven remote entries
