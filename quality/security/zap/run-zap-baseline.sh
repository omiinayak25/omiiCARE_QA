#!/usr/bin/env bash
#
# run-zap-baseline.sh — OWASP ZAP baseline (passive) scan for omiiCARE_QA
# Milestone 7 — Security Engineer
#
# EDUCATIONAL / LOCAL-INFRASTRUCTURE-ONLY.
# This script scans ONLY a locally-owned omiiCARE_QA instance
# (default: http://localhost:8080). Do NOT run it against any host you do not
# own or lack written authorization to test. Security testing of third-party
# systems without authorization is illegal.
#
# Runs zap-baseline.py from the official OWASP ZAP docker image and writes an
# HTML report. A baseline scan is passive (spider + passive rules); it does not
# send active attack payloads. For authenticated/active scanning see
# ./auth-context.md.
#
set -euo pipefail

# --- Configuration (override via environment) -------------------------------
TARGET_URL="${TARGET_URL:-http://localhost:8080}"
ZAP_IMAGE="${ZAP_IMAGE:-ghcr.io/zaproxy/zaproxy:stable}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="${CONFIG_FILE:-${SCRIPT_DIR}/zap-baseline.conf}"
REPORT_DIR="${REPORT_DIR:-${SCRIPT_DIR}/reports}"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
REPORT_HTML="zap-baseline-${TIMESTAMP}.html"
# Spider duration (minutes) and per-scan timeout safeguard.
SPIDER_MINUTES="${SPIDER_MINUTES:-2}"

# --- Guard rails ------------------------------------------------------------
echo "==> omiiCARE_QA ZAP baseline scan (local-infra-only)"
echo "    Target : ${TARGET_URL}"
echo "    Image  : ${ZAP_IMAGE}"
echo "    Config : ${CONFIG_FILE}"
echo "    Report : ${REPORT_DIR}/${REPORT_HTML}"

# Only allow loopback / private targets without an explicit override.
if [[ "${TARGET_URL}" != http://localhost* \
   && "${TARGET_URL}" != http://127.0.0.1* \
   && "${TARGET_URL}" != https://localhost* \
   && "${TARGET_URL}" != https://127.0.0.1* \
   && "${ALLOW_NONLOCAL_TARGET:-false}" != "true" ]]; then
  echo "ERROR: target '${TARGET_URL}' is not localhost." >&2
  echo "       This tool is for owned/local infrastructure only." >&2
  echo "       Set ALLOW_NONLOCAL_TARGET=true ONLY for hosts you are" >&2
  echo "       authorized to test." >&2
  exit 2
fi

if ! command -v docker >/dev/null 2>&1; then
  echo "ERROR: docker is not installed or not on PATH." >&2
  exit 3
fi

if [[ ! -f "${CONFIG_FILE}" ]]; then
  echo "ERROR: ZAP config file not found: ${CONFIG_FILE}" >&2
  exit 4
fi

mkdir -p "${REPORT_DIR}"

# --- Run the scan -----------------------------------------------------------
# The ZAP image runs as a non-root user; mount the report dir and config and
# work from /zap/wrk (the image's working directory) so outputs land in it.
# zap-baseline.py exit codes:
#   0 = no FAIL/WARN, 1 = at least one FAIL, 2 = at least one WARN (no FAIL),
#   3 = internal/config error.
set +e
docker run --rm \
  --network="host" \
  -v "${REPORT_DIR}:/zap/wrk/reports:rw" \
  -v "${CONFIG_FILE}:/zap/wrk/zap-baseline.conf:ro" \
  "${ZAP_IMAGE}" \
  zap-baseline.py \
    -t "${TARGET_URL}" \
    -c "zap-baseline.conf" \
    -m "${SPIDER_MINUTES}" \
    -r "reports/${REPORT_HTML}" \
    -I
ZAP_EXIT=$?
set -e

echo "==> ZAP exit code: ${ZAP_EXIT}"
echo "==> HTML report: ${REPORT_DIR}/${REPORT_HTML}"

# -I keeps the process exit 0 even when warnings exist, so CI can decide policy
# from the report. Surface FAILs (exit 1) and config errors (exit 3) as hard
# failures; treat WARN-only (2) as success-with-warnings.
case "${ZAP_EXIT}" in
  0) echo "==> Result: PASS (no WARN/FAIL alerts)"; exit 0 ;;
  2) echo "==> Result: PASS WITH WARNINGS (review report)"; exit 0 ;;
  1) echo "==> Result: FAIL (at least one FAIL alert)"; exit 1 ;;
  *) echo "==> Result: ERROR (ZAP internal/config error)"; exit "${ZAP_EXIT}" ;;
esac
