#!/usr/bin/env bash
# =============================================================================
# common.sh — Shared bash helpers for omiiCARE_QA developer scripts
# -----------------------------------------------------------------------------
# Sourced by setup.sh / start.sh / stop.sh / reset.sh / health-check.sh.
# Provides: color echo helpers, command detection, Java auto-detection, and a
# Docker Compose wrapper that pins the canonical compose file path.
#
# This file is meant to be *sourced*, never executed directly.
# Project:  omiiCARE_QA (Enterprise Healthcare QA platform)
# Milestone: 2 — Infrastructure Foundation
# Version:  1.0 (2026-06-30)
# =============================================================================

# --- Resolve repo root regardless of where the caller was invoked from -------
# common.sh lives at <repo>/scripts/lib/common.sh
COMMON_SH_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${COMMON_SH_DIR}/../.." && pwd)"
export REPO_ROOT

# --- Canonical, single-source-of-truth paths ---------------------------------
COMPOSE_FILE="${REPO_ROOT}/infrastructure/docker/docker-compose.yml"
ENV_FILE="${REPO_ROOT}/infrastructure/docker/.env"
ENV_EXAMPLE="${REPO_ROOT}/infrastructure/docker/.env.example"
BACKEND_DIR="${REPO_ROOT}/apps/backend"
FRONTEND_DIR="${REPO_ROOT}/apps/frontend"
export COMPOSE_FILE ENV_FILE ENV_EXAMPLE BACKEND_DIR FRONTEND_DIR

# --- Colors (auto-disabled when not a TTY or NO_COLOR is set) -----------------
if [ -t 1 ] && [ -z "${NO_COLOR:-}" ]; then
  C_RESET="\033[0m"; C_RED="\033[31m"; C_GREEN="\033[32m"
  C_YELLOW="\033[33m"; C_BLUE="\033[34m"; C_BOLD="\033[1m"
else
  C_RESET=""; C_RED=""; C_GREEN=""; C_YELLOW=""; C_BLUE=""; C_BOLD=""
fi

# --- Echo helpers ------------------------------------------------------------
say()     { printf "%b\n" "$*"; }
info()    { printf "%b\n" "${C_BLUE}[INFO]${C_RESET} $*"; }
ok()      { printf "%b\n" "${C_GREEN}[ OK ]${C_RESET} $*"; }
warn()    { printf "%b\n" "${C_YELLOW}[WARN]${C_RESET} $*" >&2; }
err()     { printf "%b\n" "${C_RED}[FAIL]${C_RESET} $*" >&2; }
section() {
  printf "\n%b\n" "${C_BOLD}${C_BLUE}=== $* ===${C_RESET}"
}

# --- Command detection -------------------------------------------------------
# has_cmd <cmd>      -> returns 0 if available
has_cmd() { command -v "$1" >/dev/null 2>&1; }

# require_cmd <cmd> <human-name> <install-hint>
# Warns (does not exit) if missing so setup can continue gracefully.
# Returns 0 if present, 1 if missing.
require_cmd() {
  local cmd="$1" name="${2:-$1}" hint="${3:-}"
  if has_cmd "$cmd"; then
    return 0
  fi
  warn "${name} not found on PATH.${hint:+ $hint}"
  return 1
}

# --- Java auto-detection -----------------------------------------------------
# Maven needs a valid JAVA_HOME. The environment's JAVA_HOME may be wrong or
# unset, so we detect a working Java 21+ generically and export JAVA_HOME.
# Echoes nothing; sets JAVA_HOME and DETECTED_JAVA on success. Returns 1 on fail.
detect_java() {
  local java_bin=""

  # 1) Honor an existing, *valid* JAVA_HOME first.
  if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
    java_bin="${JAVA_HOME}/bin/java"
  # 2) Fall back to java on PATH and derive JAVA_HOME from it.
  elif has_cmd java; then
    java_bin="$(command -v java)"
    # Resolve symlinks to find the real home (…/bin/java -> JAVA_HOME).
    local real; real="$(cd "$(dirname "$java_bin")" && pwd)"
    JAVA_HOME="$(cd "${real}/.." && pwd)"
  else
    return 1
  fi

  DETECTED_JAVA="$java_bin"
  export JAVA_HOME DETECTED_JAVA
  return 0
}

# java_major <java-binary>  -> prints the major version number (e.g. 21)
java_major() {
  local jb="${1:-java}"
  "$jb" -version 2>&1 | head -1 \
    | sed -E 's/.*version "?([0-9]+).*/\1/'
}

# --- Docker / Compose detection ---------------------------------------------
# Sets COMPOSE_CMD to the working compose invocation ("docker compose" or
# "docker-compose"). Returns 1 if Docker/Compose are unavailable.
detect_compose() {
  if ! has_cmd docker; then
    return 1
  fi
  if docker compose version >/dev/null 2>&1; then
    COMPOSE_CMD="docker compose"
  elif has_cmd docker-compose; then
    COMPOSE_CMD="docker-compose"
  else
    return 1
  fi
  export COMPOSE_CMD
  return 0
}

# compose <args...> — Run docker compose against the pinned compose file.
# Prints an actionable message and returns 1 if Docker/Compose/file missing,
# so callers can degrade gracefully instead of crashing.
compose() {
  if ! detect_compose; then
    err "Docker (with the Compose plugin) is required but not available."
    warn "Install Docker Desktop / Docker Engine: https://docs.docker.com/get-docker/"
    return 1
  fi
  if [ ! -f "$COMPOSE_FILE" ]; then
    err "Compose file not found: ${COMPOSE_FILE}"
    warn "The infrastructure compose stack has not been added to the repo yet."
    return 1
  fi
  # shellcheck disable=SC2086
  $COMPOSE_CMD -f "$COMPOSE_FILE" "$@"
}

# --- Confirmation prompt -----------------------------------------------------
# confirm "<question>"  -> returns 0 if user answers yes
confirm() {
  local prompt="${1:-Are you sure?}" reply
  printf "%b" "${C_YELLOW}${prompt} [y/N] ${C_RESET}"
  read -r reply
  case "$reply" in
    [yY]|[yY][eE][sS]) return 0 ;;
    *) return 1 ;;
  esac
}

# --- Service catalog (URL | label | credentials) -----------------------------
# Single source of truth for the URLs/credentials printed by start & health.
print_service_urls() {
  cat <<'URLS'
  Backend API        http://localhost:8080
    Swagger UI       http://localhost:8080/swagger-ui.html
    Health           http://localhost:8080/actuator/health
  Grafana            http://localhost:3000           (admin / admin)
  Prometheus         http://localhost:9090
  MailHog / Mailpit  http://localhost:8025
  MinIO Console      http://localhost:9001           (minioadmin / minioadmin)
  Keycloak           http://localhost:8081           (admin / admin)
  SonarQube          http://localhost:9000           (admin / admin)
  WireMock           http://localhost:8089/__admin
URLS
}
