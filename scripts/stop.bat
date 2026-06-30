@echo off
REM ============================================================================
REM stop.bat - Stop the omiiCARE_QA infrastructure stack (volumes preserved)
REM ----------------------------------------------------------------------------
REM Runs `docker compose down` WITHOUT -v, keeping named volumes for next start.
REM Use reset.bat to wipe volumes.
REM
REM Usage:    scripts\stop.bat
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
echo === omiiCARE_QA - Stopping infrastructure ===

where docker >nul 2>&1
if not %ERRORLEVEL%==0 (
  echo [FAIL] Docker with the Compose plugin is required to stop the stack.
  echo        If Docker is not running, there is nothing to stop.
  endlocal & exit /b 1
)
if not exist "%COMPOSE_FILE%" (
  echo [WARN] Compose file not found: %COMPOSE_FILE% - nothing to stop.
  endlocal & exit /b 0
)

echo [INFO] Running: docker compose -f ^<compose^> down (volumes preserved)
docker compose -f "%COMPOSE_FILE%" down
echo [ OK ] Infrastructure stopped. Data volumes are preserved.
echo   Re-start with: scripts\start.bat
echo   Wipe data with: scripts\reset.bat
endlocal & exit /b 0
