#!/usr/bin/env bash
# =============================================================================
# psql-connect.sh — open an interactive psql shell to the omiiCARE PostgreSQL.
#
# Connects to the local (docker) PostgreSQL using env defaults. Override any of
# DB_HOST / DB_PORT / DB_NAME / DB_USER / PGPASSWORD via the environment.
#
#   ./psql-connect.sh                 # connect with defaults
#   DB_NAME=omiicare_qa ./psql-connect.sh
#   PGPASSWORD=secret ./psql-connect.sh
#
# All seed data is synthetic / PHI-safe; never load real PHI through this shell.
# =============================================================================
set -euo pipefail

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-omiicare}"
DB_USER="${DB_USER:-omiicare}"

if ! command -v psql >/dev/null 2>&1; then
    echo "error: psql client not found on PATH" >&2
    echo "hint: install postgresql-client, or run via docker:" >&2
    echo "      docker exec -it <postgres-container> psql -U ${DB_USER} -d ${DB_NAME}" >&2
    exit 1
fi

echo "Connecting to postgresql://${DB_USER}@${DB_HOST}:${DB_PORT}/${DB_NAME} ..." >&2

# PGPASSWORD (if set in the environment) is honored by psql automatically.
exec psql \
    --host="${DB_HOST}" \
    --port="${DB_PORT}" \
    --username="${DB_USER}" \
    --dbname="${DB_NAME}" \
    "$@"
