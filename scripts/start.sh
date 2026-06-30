#!/usr/bin/env bash
# =============================================================================
# start.sh — Start the omiiCARE_QA infrastructure stack
# -----------------------------------------------------------------------------
# Brings up the Docker Compose infra stack (Postgres, Grafana, Prometheus,
# MailHog, MinIO, Keycloak, SonarQube, WireMock), waits for Postgres to become
# healthy, then prints how to run the backend plus all service URLs and creds.
#
# Usage:    ./scripts/start.sh
# Project:  omiiCARE_QA  |  Milestone 2 — Infrastructure Foundation
# Version:  1.0 (2026-06-30)
# =============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
. "${SCRIPT_DIR}/lib/common.sh"

section "omiiCARE_QA — Starting infrastructure"

# --- Preconditions -----------------------------------------------------------
if ! detect_compose; then
  err "Docker with the Compose plugin is required to start the stack."
  warn "Install Docker: https://docs.docker.com/get-docker/"
  exit 1
fi
if [ ! -f "${COMPOSE_FILE}" ]; then
  err "Compose file not found: ${COMPOSE_FILE}"
  warn "Infrastructure compose stack has not been added to the repo yet."
  exit 1
fi

# --- Bring up the stack ------------------------------------------------------
info "Compose file: ${COMPOSE_FILE}"
info "Running: ${COMPOSE_CMD} -f <compose> up -d"
compose up -d
ok "Containers requested."

# --- Wait for Postgres to be healthy ----------------------------------------
section "Waiting for PostgreSQL"
PG_SERVICE="postgres"
DEADLINE=$(( $(date +%s) + 120 ))   # up to 120s
healthy=0
while [ "$(date +%s)" -lt "${DEADLINE}" ]; do
  # Prefer container health status; fall back to pg_isready inside the container.
  cid="$(compose ps -q "${PG_SERVICE}" 2>/dev/null || true)"
  if [ -n "${cid}" ]; then
    status="$(docker inspect -f '{{.State.Health.Status}}' "${cid}" 2>/dev/null || echo "")"
    if [ "${status}" = "healthy" ]; then healthy=1; break; fi
    if [ -z "${status}" ] || [ "${status}" = "<no value>" ]; then
      # No healthcheck defined — probe directly.
      if compose exec -T "${PG_SERVICE}" pg_isready >/dev/null 2>&1; then
        healthy=1; break
      fi
    fi
  fi
  printf "."
  sleep 3
done
printf "\n"

if [ "${healthy}" -eq 1 ]; then
  ok "PostgreSQL is healthy."
else
  warn "PostgreSQL did not report healthy within the timeout."
  warn "Inspect with: ${COMPOSE_CMD} -f ${COMPOSE_FILE} ps  /  logs ${PG_SERVICE}"
fi

# --- Run-the-backend hint ----------------------------------------------------
section "Run the backend"
say "  ${C_BOLD}mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker${C_RESET}"
say "  (the 'docker' profile points the backend at the Postgres container)"

# --- Service URLs ------------------------------------------------------------
section "Service URLs & default credentials"
print_service_urls
say ""
ok "Infrastructure is up. Use ./scripts/health-check.sh to verify."
