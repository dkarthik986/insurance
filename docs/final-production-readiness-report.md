# InsureDesk production-readiness report

**Assessment date:** 2026-07-26  
**Commit:** generated after the hardening changes in this workspace

## Implemented

- Backend runs on a configurable port (default `8081`) and requires database/JWT secrets from environment variables.
- Flyway migrations add refresh-session rotation/revocation, password-reset tokens, audit/document/notification tables, export jobs and notification settings.
- Authentication now uses short-lived in-memory access tokens in the web client and an HttpOnly refresh cookie. Failed-login lockout, logout, logout-all, `/auth/me`, and password reset flows are present.
- Frontend workspaces build independently and the shell supports runtime-configured Module Federation remotes.
- Production builds no longer use demo fixtures unless `VITE_ENABLE_DEMO_DATA=true` in development.

## Verification

- `backend\mvnw.cmd -q test` — PASS
- `frontend\npm run build` — PASS (all workspaces)
- Live PostgreSQL migration — NOT RUN in this environment because the database password was not supplied.
- Render/Vercel deployment — NOT RUN because deployment credentials and project identifiers were not supplied.
- Real Gmail/WhatsApp/Cloudinary delivery — NOT RUN; configure provider secrets and run staging smoke tests.

## Required launch gates

1. Set `DB_URL`, `DB_USER`, `DB_PASS`, `JWT_SECRET`, `APP_ALLOWED_ORIGINS`, and provider secrets in the deployment secret store.
2. Run Flyway against a staging database and verify V11–V13 before production.
3. Create the first dealer through a controlled operator/bootstrap process; no fixed password is seeded by migrations.
4. Execute browser/API smoke tests with a real dealer and customer account.
5. Configure TLS, secure cookies (`AUTH_COOKIE_SECURE=true`), backups, monitoring and rollback.

This report intentionally does not claim a production launch until those environment-dependent gates pass.
