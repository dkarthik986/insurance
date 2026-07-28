# InsureDesk

Insurance agency management platform for Star Health, Tata AIG Health, LIC,
Tata AIG Vehicle, and IFFCO Tokio.

## Structure

- `frontend/` — React + TypeScript micro-frontends (ports 4000–4007)
- `backend/` — Spring Boot 3 / Java 21 REST API (default port 8082)

## Local setup

1. Copy `backend/.env.example` to `backend/.env` and set the PostgreSQL
   connection for the existing `insurance` database.
2. Frontend local values are already provided in `frontend/.env`; use
   `frontend/.env.example` as the reference when changing ports.
3. Double-click `start-frontend.cmd`, or run `npm run dev:all` from
   `frontend`.
4. Double-click `start-backend.cmd`. Alternatively, run
   `mvnw.cmd spring-boot:run` (Windows) or `./mvnw spring-boot:run` from
   `backend`.

The UI includes a demo-data fallback so it can be reviewed before the API is
running. Set `VITE_ENABLE_DEMO_DATA=false` to require live API data.
