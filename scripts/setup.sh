#!/usr/bin/env bash
# =============================================================================
# setup.sh — One-time developer environment setup for omiiCARE_QA
# -----------------------------------------------------------------------------
# Validates toolchain (Java 21+, Maven, Node, Docker), bootstraps the infra
# .env file, builds the backend, installs frontend deps (if present), and
# prints next steps. Designed to be re-runnable and to degrade gracefully when
# optional tooling (Docker) or not-yet-added modules (frontend) are missing.
#
# Usage:    ./scripts/setup.sh
# Project:  omiiCARE_QA  |  Milestone 2 — Infrastructure Foundation
# Version:  1.0 (2026-06-30)
# =============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
. "${SCRIPT_DIR}/lib/common.sh"

MISSING_REQUIRED=0

section "omiiCARE_QA — Developer Setup"
info "Repository root: ${REPO_ROOT}"

# -----------------------------------------------------------------------------
# 1. Validate toolchain
# -----------------------------------------------------------------------------
section "1/5  Validating toolchain"

# Java 21+
if detect_java; then
  JAVA_VER="$(java_major "${DETECTED_JAVA}")"
  if [ "${JAVA_VER:-0}" -ge 21 ] 2>/dev/null; then
    ok "Java ${JAVA_VER} detected (JAVA_HOME=${JAVA_HOME})"
  else
    warn "Java ${JAVA_VER:-?} detected, but Java 21+ is required for the backend."
    MISSING_REQUIRED=1
  fi
else
  warn "Java not found. Install a JDK 21+ (https://adoptium.net) and set JAVA_HOME."
  MISSING_REQUIRED=1
fi

# Maven
if require_cmd mvn "Maven" "Install Maven 3.9+ (https://maven.apache.org)."; then
  ok "Maven: $(mvn -v 2>/dev/null | head -1)"
fi

# Node (optional now; needed once frontend lands)
if require_cmd node "Node.js" "Install Node 20+ (https://nodejs.org)."; then
  ok "Node: $(node --version 2>/dev/null)"
fi

# Docker (optional for setup; required for start)
if require_cmd docker "Docker" "Install Docker Desktop / Engine (https://docs.docker.com/get-docker/)."; then
  ok "Docker: $(docker --version 2>/dev/null)"
  if detect_compose; then
    ok "Compose: $(${COMPOSE_CMD} version --short 2>/dev/null || echo "${COMPOSE_CMD}")"
  else
    warn "Docker Compose plugin not found; 'start' and 'health-check' need it."
  fi
else
  warn "Docker is not required for setup, but you will need it to run the infra stack."
fi

# -----------------------------------------------------------------------------
# 2. Bootstrap infrastructure .env
# -----------------------------------------------------------------------------
section "2/5  Infrastructure environment file"

if [ -f "${ENV_FILE}" ]; then
  ok ".env already present: ${ENV_FILE}"
elif [ -f "${ENV_EXAMPLE}" ]; then
  cp "${ENV_EXAMPLE}" "${ENV_FILE}"
  ok "Created ${ENV_FILE} from .env.example"
else
  warn ".env.example not found at ${ENV_EXAMPLE}; skipping (infra compose not yet added)."
fi

# -----------------------------------------------------------------------------
# 3. Build backend
# -----------------------------------------------------------------------------
section "3/5  Building backend (apps/backend)"

if [ -f "${REPO_ROOT}/pom.xml" ] && has_cmd mvn && [ "${MISSING_REQUIRED}" -eq 0 ]; then
  info "Running: mvn -q -DskipTests install   (JAVA_HOME=${JAVA_HOME})"
  if ( cd "${REPO_ROOT}" && JAVA_HOME="${JAVA_HOME}" mvn -q -DskipTests install ); then
    ok "Backend build succeeded."
  else
    err "Backend build failed. Check the Maven output above."
    MISSING_REQUIRED=1
  fi
else
  warn "Skipping backend build (Maven or a valid Java 21+ is missing, or no pom.xml)."
fi

# -----------------------------------------------------------------------------
# 4. Install frontend dependencies (if module present)
# -----------------------------------------------------------------------------
section "4/5  Frontend dependencies (apps/frontend)"

if [ -f "${FRONTEND_DIR}/package.json" ]; then
  if has_cmd npm; then
    info "Running: npm ci  (in ${FRONTEND_DIR})"
    if ( cd "${FRONTEND_DIR}" && npm ci ); then
      ok "Frontend dependencies installed."
    else
      warn "npm ci failed; try 'npm install' or check the Node version."
    fi
  else
    warn "npm not found; install Node.js to set up the frontend."
  fi
else
  info "No apps/frontend/package.json yet — frontend not added. Skipping."
fi

# -----------------------------------------------------------------------------
# 5. Next steps
# -----------------------------------------------------------------------------
section "5/5  Next steps"
say "  1. Start infrastructure:  ${C_BOLD}./scripts/start.sh${C_RESET}"
say "  2. Run the backend:       ${C_BOLD}mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker${C_RESET}"
say "  3. Check service health:  ${C_BOLD}./scripts/health-check.sh${C_RESET}"
say "  4. Stop infrastructure:   ${C_BOLD}./scripts/stop.sh${C_RESET}"
say ""

if [ "${MISSING_REQUIRED}" -ne 0 ]; then
  warn "Setup finished with warnings — resolve the items above before starting."
  exit 1
fi
ok "Setup complete."
