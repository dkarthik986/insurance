# Production-readiness audit

Audit baseline: 26 July 2026  
Repository: `insurance`  
Scope: Spring Boot API, PostgreSQL/Flyway, shell + seven React MFEs, Render/Vercel deployment

## Existing structure

- `backend/` — Spring Boot 3.2.5, Java 21, JPA, Flyway, JWT, mail, Cloudinary, Excel/PDF endpoints.
- `frontend/shell-app/` — authenticated shell, navigation, layouts, local fallback views, Module Federation host.
- `frontend/mfe-*` — seven independently buildable Vite remotes with `remoteEntry.js`.
- `frontend/.env*` — local/production endpoint and port configuration.
- `docs/` — API, phase and operational notes.

## Baseline implemented features

| Area | Baseline |
| --- | --- |
| Build | Frontend workspaces build; backend compiles/packages |
| Database | Ten initial Flyway migrations and PostgreSQL 14/18 local services |
| Domain | Users, customers, family members, vehicles, policies, LIC schedules, claims, notifications, follow-ups |
| Auth | JWT access token, BCrypt password verification, role route rules |
| Integrations | JavaMail, WhatsApp Cloud API, Cloudinary upload adapter |
| Reports | Excel and PDF export endpoints |
| UI | Responsive advisor shell, dashboard, list screens, wizard, customer portal preview |
| Deployment | Dockerfile, Render manifest, eight Vercel manifests |

## Critical gaps found

| Area | Risk | Status before remediation |
| --- | --- | --- |
| Authentication | Demo boolean local-storage login bypass; refresh tokens returned in JSON and stored directly | FAIL |
| Secrets | Sensitive fallback values and fixed bootstrap dealer password in migration | FAIL |
| Ownership | Portal records need explicit per-resource ownership checks | PARTIAL |
| Frontend data | Shell list pages use demo fixtures as a local fallback and do not yet expose complete CRUD detail workflows | PARTIAL |
| DTOs | Several write endpoints accept `Map<String, String/Object>` | PARTIAL |
| Persistence | Missing session, reset-token, audit, document metadata and notification-attempt tables | FAIL |
| Validation | Cross-field policy/claim/file validation incomplete | PARTIAL |
| Observability | No trace ID/audit event pipeline; provider failures are not consistently persisted | FAIL |
| Tests | Small MockMvc/unit baseline; no Testcontainers, frontend tests, E2E or CI workflows | FAIL |
| Deployment | Manifests exist but account credentials, production origins and live smoke tests are not available | NOT TESTED |

## Planned implementation order

1. Secrets, session-based refresh cookies, current-user and route authorization.
2. Forward-only security/audit/document/notification migrations and typed request DTOs.
3. Ownership and workflow validation across customers, policies, vehicles, claims and portal.
4. Provider attempts/retry, report safety, request tracing and operational health.
5. Shared frontend foundation, API hooks, loading/error/empty states and removal of production demo auth/data.
6. Remote MFE boundaries and complete portal/dealer workflows.
7. Testcontainers/API/frontend/E2E tests, CI and deployment documentation.

## Files to be modified

- `backend/src/main/resources/application.properties`
- `backend/src/main/java/com/insurance/agent/config/*`
- `backend/src/main/java/com/insurance/agent/auth/*`
- `backend/src/main/java/com/insurance/agent/common/*`
- domain services/controllers under `customer`, `policy`, `vehicle`, `claim`, `notification`, `report`, `portal`
- `frontend/shell-app/src/*`
- each `frontend/mfe-*` package and Vite federation config
- `.gitignore`, `README.md`, deployment manifests

## Files to be added

- forward-only Flyway migrations for sessions, password resets, audit events, document metadata and notification attempts
- shared frontend package under `frontend/shared/`
- backend audit, trace and provider-attempt modules
- backend and frontend test suites
- GitHub Actions and operational documentation

## Risks and controls

- Authentication changes can invalidate existing sessions; old refresh-token columns remain readable only during migration and are then retired forward-only.
- New constraints must be added as `NOT VALID`, backfilled/verified, then validated to avoid destructive changes to existing records.
- Removing demo fallback affects local preview; a clearly named development fixture flag remains available only for local development.
- Cross-origin cookies require exact production origins and HTTPS; local mode uses `Secure=false` only under an explicit development profile.

## Progress

This document is updated after each remediation stage. No final production-ready claim is made until critical security, ownership, migration and build/test checks are PASS.

