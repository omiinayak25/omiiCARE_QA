#!/usr/bin/env bash
# =============================================================================
# flyway-info.sh — show Flyway migration status for omiiCARE_QA.
#
# The live migrations live in apps/backend and run automatically via Spring Boot
# Flyway on application startup (no standalone Flyway install required). This
# helper prints migration status and documents the ways to inspect it.
#
#   ./flyway-info.sh            # attempt `mvn -pl apps/backend flyway:info`
#   ./flyway-info.sh --help     # show all inspection options
# =============================================================================
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

usage() {
    cat <<'EOF'
Flyway migration status — omiiCARE_QA

Migrations are owned by Flyway and applied by Spring Boot on startup. To inspect
the applied/pending state you have three options:

  1. Maven Flyway plugin (if configured in apps/backend/pom.xml):
       mvn -pl apps/backend flyway:info
       mvn -pl apps/backend flyway:validate

  2. Spring Boot on startup:
       Run the backend with logging.level.org.flywaydb=DEBUG — Flyway logs each
       versioned/repeatable migration it applies and the resulting schema state.

  3. Query the history table directly (any psql session):
       SELECT installed_rank, version, description, type, success
       FROM flyway_schema_history ORDER BY installed_rank;
     (use ./psql-connect.sh to open the shell)

Live locations:
  apps/backend/src/main/resources/db/migration   (versioned V*)
  apps/backend/src/main/resources/db/seed         (repeatable R*, non-prod)
EOF
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    usage
    exit 0
fi

if command -v mvn >/dev/null 2>&1; then
    echo "Running: mvn -pl apps/backend flyway:info" >&2
    echo "(if the flyway plugin is not configured, see ./flyway-info.sh --help)" >&2
    cd "${REPO_ROOT}"
    exec mvn -pl apps/backend flyway:info "$@"
fi

echo "error: mvn not found on PATH." >&2
echo "Showing inspection options instead:" >&2
echo >&2
usage
exit 1
