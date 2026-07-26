@echo off
setlocal
if not defined DB_HOST set "DB_HOST=localhost"
if not defined DB_PORT set "DB_PORT=5432"
if not defined DB_NAME set "DB_NAME=insurance"
if not defined DB_USER set "DB_USER=postgres"
if not defined DB_PASS (
  echo DB_PASS is not set. Run: set DB_PASS=your_postgres_password
  exit /b 1
)
set "PGPASSWORD=%DB_PASS%"
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -h "%DB_HOST%" -p "%DB_PORT%" -U "%DB_USER%" -d "%DB_NAME%" -c "select current_database(), current_user;"
if errorlevel 1 exit /b 1
echo Database connection succeeded.
echo Flyway migrations run automatically when start-backend.cmd starts Spring Boot.

