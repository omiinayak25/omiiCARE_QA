#!/usr/bin/env bash
# =============================================================================
# health-check.sh — Health probe for omiiCARE_QA services
# -----------------------------------------------------------------------------
# Checks the backend /actuator/health endpoint, the running state of each infra
# container (via `docker compose ps`), and HTTP reachability of Grafana,
# Prometheus, MailHog, MinIO, Keycloak, SonarQube and WireMock. Prints a
# PASS/FAIL table and exits non-zero if any *critical* service is down.
#
# Usage:    ./scripts/health-check.sh
# Project:  omiiCARE_QA  |  Milestone 2 — Infrastructure Foundation
# Version:  1.0 (2026-06-30)
# =============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
. "${SCRIPT_DIR}/lib/common.sh"

FAILED_CRITICAL=0
ROWS=()   # accumulated "service|status|detail" rows

# record <service> <PASS|FAIL|WARN> <detail> <critical:0|1>
record() {
  local svc="$1" st="$2" detail="$3" critical="${4:-0}"
  ROWS+=("${svc}|${st}|${detail}")
  if [ "${st}" = "FAIL" ] && [ "${critical}" -eq 1 ]; then
    FAILED_CRITICAL=1
  fi
}

# http_ok <url> — returns 0 if HTTP status is < 400 (or 401/403 = "up but auth")
http_ok() {
  local url="$1" code
  if ! has_cmd curl; then return 2; fi
  # curl prints the status code via -w; on connection failure it prints 000.
  code="$(curl -s -o /dev/null -m 5 -w '%{http_code}' "$url" 2>/dev/null)"
  code="${code:-000}"
  case "$code" in
    2*|3*|401|403) echo "$code"; return 0 ;;
    *) echo "$code"; return 1 ;;
  esac
}

section "omiiCARE_QA — Health check"

if ! has_cmd curl; then
  warn "curl not found; HTTP probes will be skipped."
fi

# -----------------------------------------------------------------------------
# 1. Backend /actuator/health (critical)
# -----------------------------------------------------------------------------
BACKEND_HEALTH="http://localhost:8080/actuator/health"
if has_cmd curl; then
  body="$(curl -s -m 5 "${BACKEND_HEALTH}" 2>/dev/null || echo "")"
  if [[ "${body}" == *'"status":"UP"'* ]]; then
    record "Backend (8080)" "PASS" "actuator UP" 1
  elif [ -n "${body}" ]; then
    record "Backend (8080)" "WARN" "responded, status not UP" 1
  else
    record "Backend (8080)" "FAIL" "no response" 1
  fi
else
  record "Backend (8080)" "WARN" "curl missing" 0
fi

# -----------------------------------------------------------------------------
# 2. Infra container states via docker compose ps
# -----------------------------------------------------------------------------
if detect_compose && [ -f "${COMPOSE_FILE}" ]; then
  # Map of service-name -> "running"/"exited"/absent
  if ps_out="$(compose ps --format '{{.Service}} {{.State}}' 2>/dev/null)"; then
    if [ -n "${ps_out}" ]; then
      while read -r svc state; do
        [ -z "${svc}" ] && continue
        # Postgres is critical; others are non-critical infra.
        crit=0; [ "${svc}" = "postgres" ] && crit=1
        if [ "${state}" = "running" ]; then
          record "ctr:${svc}" "PASS" "${state}" "${crit}"
        else
          record "ctr:${svc}" "FAIL" "${state:-absent}" "${crit}"
        fi
      done <<< "${ps_out}"
    else
      record "Docker stack" "FAIL" "no containers running" 1
    fi
  else
    record "Docker stack" "FAIL" "compose ps failed" 1
  fi
else
  record "Docker stack" "WARN" "docker/compose unavailable" 0
fi

# -----------------------------------------------------------------------------
# 3. HTTP reachability of infra UIs (non-critical)
# -----------------------------------------------------------------------------
probe() {
  local name="$1" url="$2"
  if ! has_cmd curl; then record "${name}" "WARN" "curl missing" 0; return; fi
  local code rc
  # Capture both output and status without tripping `set -e` on a non-zero rc.
  code="$(http_ok "${url}")" && rc=0 || rc=$?
  if [ "${rc}" -eq 0 ]; then
    record "${name}" "PASS" "HTTP ${code}" 0
  else
    record "${name}" "FAIL" "HTTP ${code}" 0
  fi
}

probe "Grafana (3000)"      "http://localhost:3000/api/health"
probe "Prometheus (9090)"   "http://localhost:9090/-/healthy"
probe "MailHog (8025)"      "http://localhost:8025/"
probe "MinIO (9001)"        "http://localhost:9001/"
probe "Keycloak (8081)"     "http://localhost:8081/"
probe "SonarQube (9000)"    "http://localhost:9000/api/system/status"
probe "WireMock (8089)"     "http://localhost:8089/__admin/"

# -----------------------------------------------------------------------------
# 4. Render table
# -----------------------------------------------------------------------------
section "Results"
printf "%b\n" "  ${C_BOLD}SERVICE                     STATUS   DETAIL${C_RESET}"
printf "  %s\n" "-------------------------------------------------------"
for row in "${ROWS[@]}"; do
  IFS='|' read -r svc st detail <<< "${row}"
  case "${st}" in
    PASS) color="${C_GREEN}" ;;
    FAIL) color="${C_RED}" ;;
    *)    color="${C_YELLOW}" ;;
  esac
  printf "  %-26s ${color}%-6s${C_RESET}   %s\n" "${svc}" "${st}" "${detail}"
done
say ""

if [ "${FAILED_CRITICAL}" -ne 0 ]; then
  err "One or more CRITICAL services are down."
  exit 1
fi
ok "All critical services healthy."
