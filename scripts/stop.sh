#!/usr/bin/env bash
# =============================================================================
# stop.sh — Stop the omiiCARE_QA infrastructure stack (volumes preserved)
# -----------------------------------------------------------------------------
# Runs `docker compose down` WITHOUT -v, so named volumes (DB data, MinIO
# objects, etc.) are kept for the next start. Use reset.sh to wipe volumes.
#
# Usage:    ./scripts/stop.sh
# Project:  omiiCARE_QA  |  Milestone 2 — Infrastructure Foundation
# Version:  1.0 (2026-06-30)
# =============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
. "${SCRIPT_DIR}/lib/common.sh"

section "omiiCARE_QA — Stopping infrastructure"

if ! detect_compose; then
  err "Docker with the Compose plugin is required to stop the stack."
  warn "If Docker is not running, there is nothing to stop."
  exit 1
fi
if [ ! -f "${COMPOSE_FILE}" ]; then
  warn "Compose file not found: ${COMPOSE_FILE} — nothing to stop."
  exit 0
fi

info "Running: ${COMPOSE_CMD} -f <compose> down (volumes preserved)"
compose down
ok "Infrastructure stopped. Data volumes are preserved."
say "  Re-start with: ${C_BOLD}./scripts/start.sh${C_RESET}"
say "  Wipe data with: ${C_BOLD}./scripts/reset.sh${C_RESET}"
