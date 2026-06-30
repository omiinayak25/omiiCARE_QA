@echo off
REM ============================================================================
REM reset.bat - DESTRUCTIVE full reset of the omiiCARE_QA dev env (Windows)
REM ----------------------------------------------------------------------------
REM Tears the stack down WITH volumes (docker compose down -v) and cleans the
REM Maven build output. Requires confirmation, or pass --yes to skip the prompt.
REM
REM Usage:    scripts\reset.bat [--yes]
REM Project:  omiiCARE_QA  |  Milestone 2 - Infrastructure Foundation
REM Version:  1.0 (2026-06-30)
REM ============================================================================
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
pushd "%SCRIPT_DIR%.." >nul
set "REPO_ROOT=%CD%"
popd >nul
set "COMPOSE_FILE=%REPO_ROOT%\infrastructure\docker\docker-compose.yml"

set "ASSUME_YES=0"
if /i "%~1"=="--yes" set "ASSUME_YES=1"
if /i "%~1"=="-y" set "ASSUME_YES=1"

echo.
echo === omiiCARE_QA - RESET (destructive) ===
echo [WARN] This will:
echo [WARN]   * remove ALL Docker volumes for the stack (DB, MinIO, Keycloak, Sonar...)
echo [WARN]   * delete Maven build output (target\ directories)
echo [WARN] All local data and build artifacts will be permanently lost.

if not "%ASSUME_YES%"=="1" (
  set /p "REPLY=Proceed with the destructive reset? [y/N] "
  if /i not "!REPLY!"=="y" if /i not "!REPLY!"=="yes" (
    echo [INFO] Aborted. Nothing was changed.
    endlocal & exit /b 0
  )
)

echo.
echo === Removing Docker stack and volumes ===
where docker >nul 2>&1
if %ERRORLEVEL%==0 (
  if exist "%COMPOSE_FILE%" (
    echo [INFO] Running: docker compose -f ^<compose^> down -v
    docker compose -f "%COMPOSE_FILE%" down -v
    echo [ OK ] Stack and volumes removed.
  ) else (
    echo [WARN] Compose file unavailable - skipping container teardown.
  )
) else (
  echo [WARN] Docker unavailable - skipping container teardown.
)

echo.
echo === Cleaning Maven build output ===
if exist "%REPO_ROOT%\pom.xml" (
  where mvn >nul 2>&1
  if !ERRORLEVEL!==0 (
    echo [INFO] Running: mvn -q clean
    pushd "%REPO_ROOT%"
    call mvn -q clean
    popd
    echo [ OK ] Maven clean completed.
  ) else (
    echo [WARN] Maven unavailable - removing target\ directories directly.
    for /d /r "%REPO_ROOT%" %%d in (target) do if exist "%%d" rd /s /q "%%d"
    echo [ OK ] Removed target\ directories.
  )
) else (
  echo [WARN] No pom.xml - removing target\ directories directly.
  for /d /r "%REPO_ROOT%" %%d in (target) do if exist "%%d" rd /s /q "%%d"
)

echo.
echo [ OK ] Reset complete. Run scripts\setup.bat to rebuild.
endlocal & exit /b 0
