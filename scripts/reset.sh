#!/usr/bin/env bash
# =============================================================================
# reset.sh — DESTRUCTIVE full reset of the omiiCARE_QA dev environment
# -----------------------------------------------------------------------------
# Tears the stack down WITH volumes (`docker compose down -v`) — removing all
# database data, MinIO objects, Keycloak realms, etc. — and cleans the Maven
# build output. Requires explicit confirmation, or pass --yes to skip the prompt.
#
# Usage:    ./scripts/reset.sh [--yes]
# Project:  omiiCARE_QA  |  Milestone 2 — Infrastructure Foundation
# Version:  1.0 (2026-06-30)
# =============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
. "${SCRIPT_DIR}/lib/common.sh"

ASSUME_YES=0
for arg in "$@"; do
  case "$arg" in
    --yes|-y) ASSUME_YES=1 ;;
    *) warn "Unknown argument: $arg" ;;
  esac
done

section "omiiCARE_QA — RESET (destructive)"
warn "This will:"
warn "  * remove ALL Docker volumes for the stack (DB, MinIO, Keycloak, Sonar...)"
warn "  * delete Maven build output (target/ directories)"
warn "All local data and build artifacts will be permanently lost."

if [ "${ASSUME_YES}" -ne 1 ]; then
  if ! confirm "Proceed with the destructive reset?"; then
    info "Aborted. Nothing was changed."
    exit 0
  fi
fi

# --- Tear down with volumes --------------------------------------------------
section "Removing Docker stack and volumes"
if detect_compose && [ -f "${COMPOSE_FILE}" ]; then
  info "Running: ${COMPOSE_CMD} -f <compose> down -v"
  compose down -v
  ok "Stack and volumes removed."
else
  warn "Docker/Compose or compose file unavailable — skipping container teardown."
fi

# --- Clean Maven build output ------------------------------------------------
section "Cleaning Maven build output"
if [ -f "${REPO_ROOT}/pom.xml" ] && has_cmd mvn && detect_java; then
  info "Running: mvn -q clean   (JAVA_HOME=${JAVA_HOME})"
  if ( cd "${REPO_ROOT}" && JAVA_HOME="${JAVA_HOME}" mvn -q clean ); then
    ok "Maven 'clean' completed."
  else
    warn "mvn clean failed; removing target/ directories directly."
    find "${REPO_ROOT}" -type d -name target -prune -exec rm -rf {} + 2>/dev/null || true
    ok "Removed target/ directories."
  fi
else
  warn "Maven/Java unavailable — removing target/ directories directly."
  find "${REPO_ROOT}" -type d -name target -prune -exec rm -rf {} + 2>/dev/null || true
  ok "Removed target/ directories."
fi

say ""
ok "Reset complete. Run ${C_BOLD}./scripts/setup.sh${C_RESET} to rebuild."
