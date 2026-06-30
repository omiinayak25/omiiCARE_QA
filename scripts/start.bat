@echo off
REM ============================================================================
REM start.bat - Start the omiiCARE_QA infrastructure stack (Windows)
REM ----------------------------------------------------------------------------
REM Brings up the Docker Compose infra stack, waits for Postgres to be healthy,
REM then prints how to run the backend plus all service URLs and credentials.
REM
REM Usage:    scripts\start.bat
REM Project:  omiiCARE_QA  |  Milestone 2 - Infrastructure Foundation
REM Version:  1.0 (2026-06-30)
REM ============================================================================
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
pushd "%SCRIPT_DIR%.." >nul
set "REPO_ROOT=%CD%"
popd >nul
set "COMPOSE_FILE=%REPO_ROOT%\infrastructure\docker\docker-compose.yml"

echo.
echo === omiiCARE_QA - Starting infrastructure ===

where docker >nul 2>&1
if not %ERRORLEVEL%==0 (
  echo [FAIL] Docker with the Compose plugin is required to start the stack.
  echo        Install: https://docs.docker.com/get-docker/
  endlocal & exit /b 1
)
if not exist "%COMPOSE_FILE%" (
  echo [FAIL] Compose file not found: %COMPOSE_FILE%
  echo        Infrastructure compose stack has not been added to the repo yet.
  endlocal & exit /b 1
)

echo [INFO] Compose file: %COMPOSE_FILE%
echo [INFO] Running: docker compose -f ^<compose^> up -d
docker compose -f "%COMPOSE_FILE%" up -d
if not %ERRORLEVEL%==0 ( echo [FAIL] docker compose up failed. & endlocal & exit /b 1 )
echo [ OK ] Containers requested.

echo.
echo === Waiting for PostgreSQL ===
set "HEALTHY=0"
for /l %%i in (1,1,40) do (
  if "!HEALTHY!"=="0" (
    for /f %%c in ('docker compose -f "%COMPOSE_FILE%" ps -q postgres 2^>nul') do (
      for /f %%s in ('docker inspect -f "{{.State.Health.Status}}" %%c 2^>nul') do (
        if "%%s"=="healthy" set "HEALTHY=1"
      )
    )
    if "!HEALTHY!"=="0" (
      <nul set /p "=."
      timeout /t 3 /nobreak >nul
    )
  )
)
echo.
if "!HEALTHY!"=="1" (
  echo [ OK ] PostgreSQL is healthy.
) else (
  echo [WARN] PostgreSQL did not report healthy within the timeout.
  echo        Inspect with: docker compose -f "%COMPOSE_FILE%" ps  /  logs postgres
)

echo.
echo === Run the backend ===
echo   mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker
echo   ^(the 'docker' profile points the backend at the Postgres container^)

echo.
echo === Service URLs ^& default credentials ===
echo   Backend API        http://localhost:8080
echo     Swagger UI       http://localhost:8080/swagger-ui.html
echo     Health           http://localhost:8080/actuator/health
echo   Grafana            http://localhost:3000           ^(admin / admin^)
echo   Prometheus         http://localhost:9090
echo   MailHog / Mailpit  http://localhost:8025
echo   MinIO Console      http://localhost:9001           ^(minioadmin / minioadmin^)
echo   Keycloak           http://localhost:8081           ^(admin / admin^)
echo   SonarQube          http://localhost:9000           ^(admin / admin^)
echo   WireMock           http://localhost:8089/__admin
echo.
echo [ OK ] Infrastructure is up. Use scripts\health-check.bat to verify.
endlocal & exit /b 0
