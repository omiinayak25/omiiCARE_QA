@echo off
REM ============================================================================
REM health-check.bat - Health probe for omiiCARE_QA services (Windows)
REM ----------------------------------------------------------------------------
REM Checks the backend /actuator/health endpoint, the running state of infra
REM containers (docker compose ps), and HTTP reachability of the infra UIs.
REM Prints a PASS/FAIL table and exits non-zero if a critical service is down.
REM
REM Usage:    scripts\health-check.bat
REM Project:  omiiCARE_QA  |  Milestone 2 - Infrastructure Foundation
REM Version:  1.0 (2026-06-30)
REM ============================================================================
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
pushd "%SCRIPT_DIR%.." >nul
set "REPO_ROOT=%CD%"
popd >nul
set "COMPOSE_FILE=%REPO_ROOT%\infrastructure\docker\docker-compose.yml"
set "FAILED_CRITICAL=0"

echo.
echo === omiiCARE_QA - Health check ===

where curl >nul 2>&1
set "HAVE_CURL=%ERRORLEVEL%"
if not "%HAVE_CURL%"=="0" echo [WARN] curl not found; HTTP probes will be skipped.

echo.
echo   SERVICE                     STATUS   DETAIL
echo   -------------------------------------------------------

REM --- Backend /actuator/health (critical) ------------------------------------
if "%HAVE_CURL%"=="0" (
  for /f %%h in ('curl -s -m 5 "http://localhost:8080/actuator/health" ^| findstr /c:"\"status\":\"UP\""') do set "BUP=1"
  if defined BUP (
    call :row "Backend (8080)" PASS "actuator UP"
  ) else (
    call :row "Backend (8080)" FAIL "no UP response"
    set "FAILED_CRITICAL=1"
  )
) else (
  call :row "Backend (8080)" WARN "curl missing"
)

REM --- Infra container states -------------------------------------------------
where docker >nul 2>&1
if %ERRORLEVEL%==0 if exist "%COMPOSE_FILE%" (
  set "ANY=0"
  for /f "tokens=1,2" %%a in ('docker compose -f "%COMPOSE_FILE%" ps --format "{{.Service}} {{.State}}" 2^>nul') do (
    set "ANY=1"
    if "%%b"=="running" (
      call :row "ctr:%%a" PASS "%%b"
    ) else (
      call :row "ctr:%%a" FAIL "%%b"
      if "%%a"=="postgres" set "FAILED_CRITICAL=1"
    )
  )
  if "!ANY!"=="0" (
    call :row "Docker stack" FAIL "no containers running"
    set "FAILED_CRITICAL=1"
  )
) else (
  call :row "Docker stack" WARN "docker/compose unavailable"
)

REM --- HTTP reachability of infra UIs (non-critical) --------------------------
call :probe "Grafana (3000)"    "http://localhost:3000/api/health"
call :probe "Prometheus (9090)" "http://localhost:9090/-/healthy"
call :probe "MailHog (8025)"    "http://localhost:8025/"
call :probe "MinIO (9001)"      "http://localhost:9001/"
call :probe "Keycloak (8081)"   "http://localhost:8081/"
call :probe "SonarQube (9000)"  "http://localhost:9000/api/system/status"
call :probe "WireMock (8089)"   "http://localhost:8089/__admin/"

echo.
if not "%FAILED_CRITICAL%"=="0" (
  echo [FAIL] One or more CRITICAL services are down.
  endlocal & exit /b 1
)
echo [ OK ] All critical services healthy.
endlocal & exit /b 0

REM --- subroutines ------------------------------------------------------------
:row
REM %~1 service  %~2 status  %~3 detail
set "S=%~1                          "
set "S=!S:~0,26!"
echo   !S! %~2     %~3
exit /b 0

:probe
REM %~1 name  %~2 url
if not "%HAVE_CURL%"=="0" ( call :row %1 WARN "curl missing" & exit /b 0 )
for /f %%c in ('curl -s -o NUL -m 5 -w "%%{http_code}" %2 2^>nul') do set "CODE=%%c"
set "OKHTTP=0"
if "!CODE:~0,1!"=="2" set "OKHTTP=1"
if "!CODE:~0,1!"=="3" set "OKHTTP=1"
if "!CODE!"=="401" set "OKHTTP=1"
if "!CODE!"=="403" set "OKHTTP=1"
if "!OKHTTP!"=="1" (
  call :row %1 PASS "HTTP !CODE!"
) else (
  call :row %1 FAIL "HTTP !CODE!"
)
exit /b 0
