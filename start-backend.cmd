@echo off
cd /d "%~dp0backend"
if not defined DB_URL set "DB_URL=jdbc:postgresql://localhost:5432/insurance"
if not defined DB_USER set "DB_USER=postgres"
if not defined DB_PASS (
  echo DB_PASS is not set.
  echo Run: set DB_PASS=your_postgres_password
  echo Then run this file again.
  exit /b 1
)
echo Starting Insurance API at http://localhost:8081
call mvnw.cmd spring-boot:run
