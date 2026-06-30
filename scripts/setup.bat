@echo off
REM ============================================================================
REM setup.bat - One-time developer environment setup for omiiCARE_QA (Windows)
REM ----------------------------------------------------------------------------
REM Validates toolchain (Java 21+, Maven, Node, Docker), bootstraps the infra
REM .env file, builds the backend, installs frontend deps (if present), and
REM prints next steps. Degrades gracefully when Docker or the frontend module
REM are missing.
REM
REM Usage:    scripts\setup.bat
REM Project:  omiiCARE_QA  |  Milestone 2 - Infrastructure Foundation
REM Version:  1.0 (2026-06-30)
REM ============================================================================
setlocal enabledelayedexpansion

REM --- Resolve repo root (this file lives in <repo>\scripts) -------------------
set "SCRIPT_DIR=%~dp0"
pushd "%SCRIPT_DIR%.." >nul
set "REPO_ROOT=%CD%"
popd >nul

set "COMPOSE_FILE=%REPO_ROOT%\infrastructure\docker\docker-compose.yml"
set "ENV_FILE=%REPO_ROOT%\infrastructure\docker\.env"
set "ENV_EXAMPLE=%REPO_ROOT%\infrastructure\docker\.env.example"
set "FRONTEND_DIR=%REPO_ROOT%\apps\frontend"
set "MISSING_REQUIRED=0"

echo.
echo === omiiCARE_QA - Developer Setup ===
echo [INFO] Repository root: %REPO_ROOT%

echo.
echo === 1/5  Validating toolchain ===

REM Java 21+
where java >nul 2>&1
if %ERRORLEVEL%==0 (
  for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set "JV=%%~v"
  )
  for /f "delims=. tokens=1" %%a in ("!JV!") do set "JMAJOR=%%a"
  if !JMAJOR! GEQ 21 (
    echo [ OK ] Java !JMAJOR! detected
  ) else (
    echo [WARN] Java !JMAJOR! detected, but Java 21+ is required.
    set "MISSING_REQUIRED=1"
  )
) else (
  echo [WARN] Java not found. Install JDK 21+ ^(https://adoptium.net^) and set JAVA_HOME.
  set "MISSING_REQUIRED=1"
)

REM Maven
where mvn >nul 2>&1
if %ERRORLEVEL%==0 ( echo [ OK ] Maven found ) else ( echo [WARN] Maven not found ^(https://maven.apache.org^). )

REM Node
where node >nul 2>&1
if %ERRORLEVEL%==0 ( for /f %%n in ('node --version') do echo [ OK ] Node %%n ) else ( echo [WARN] Node.js not found ^(https://nodejs.org^). )

REM Docker
where docker >nul 2>&1
if %ERRORLEVEL%==0 (
  echo [ OK ] Docker found
) else (
  echo [WARN] Docker not found. Not required for setup, but needed to run the stack.
  echo        Install: https://docs.docker.com/get-docker/
)

echo.
echo === 2/5  Infrastructure environment file ===
if exist "%ENV_FILE%" (
  echo [ OK ] .env already present: %ENV_FILE%
) else (
  if exist "%ENV_EXAMPLE%" (
    copy /Y "%ENV_EXAMPLE%" "%ENV_FILE%" >nul
    echo [ OK ] Created %ENV_FILE% from .env.example
  ) else (
    echo [WARN] .env.example not found; skipping ^(infra compose not yet added^).
  )
)

echo.
echo === 3/5  Building backend (apps\backend) ===
if exist "%REPO_ROOT%\pom.xml" (
  where mvn >nul 2>&1
  if !ERRORLEVEL!==0 if !MISSING_REQUIRED!==0 (
    echo [INFO] Running: mvn -q -DskipTests install
    pushd "%REPO_ROOT%"
    call mvn -q -DskipTests install
    if !ERRORLEVEL!==0 ( echo [ OK ] Backend build succeeded. ) else ( echo [FAIL] Backend build failed. & set "MISSING_REQUIRED=1" )
    popd
  ) else (
    echo [WARN] Skipping backend build ^(Maven or valid Java 21+ missing^).
  )
) else (
  echo [WARN] No pom.xml found; skipping backend build.
)

echo.
echo === 4/5  Frontend dependencies (apps\frontend) ===
if exist "%FRONTEND_DIR%\package.json" (
  where npm >nul 2>&1
  if !ERRORLEVEL!==0 (
    echo [INFO] Running: npm ci
    pushd "%FRONTEND_DIR%"
    call npm ci
    if !ERRORLEVEL!==0 ( echo [ OK ] Frontend dependencies installed. ) else ( echo [WARN] npm ci failed. )
    popd
  ) else (
    echo [WARN] npm not found; install Node.js to set up the frontend.
  )
) else (
  echo [INFO] No apps\frontend\package.json yet - skipping.
)

echo.
echo === 5/5  Next steps ===
echo   1. Start infrastructure:  scripts\start.bat
echo   2. Run the backend:       mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker
echo   3. Check service health:  scripts\health-check.bat
echo   4. Stop infrastructure:   scripts\stop.bat
echo.

if not "%MISSING_REQUIRED%"=="0" (
  echo [WARN] Setup finished with warnings - resolve the items above before starting.
  endlocal & exit /b 1
)
echo [ OK ] Setup complete.
endlocal & exit /b 0
