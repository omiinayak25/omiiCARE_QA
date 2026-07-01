// =============================================================================
// OpenMRS Reference Application — k6 Smoke / Light Load Script
// =============================================================================
//
// PURPOSE
//   A low-intensity "smoke" performance check for an OpenMRS Reference
//   Application instance. It exercises the public health endpoints (FHIR
//   metadata / CapabilityStatement and the REST session endpoint) under a
//   small, controlled virtual-user (VU) load and asserts latency + correctness
//   thresholds. It is meant to confirm the system is up and responsive — NOT to
//   find its breaking point.
//
// !!  OWNED-ENVIRONMENT ONLY  !!
//   Load testing generates concentrated traffic that can degrade or take down a
//   server and may be treated as a denial-of-service attack. RUN THIS ONLY
//   against an OpenMRS instance that YOU OWN or are EXPLICITLY AUTHORIZED to
//   load test (e.g. a local Docker stack at http://localhost or a dedicated QA
//   box). NEVER point this at the shared community demo (o2.openmrs.org) or any
//   third-party / production system. The default BASE_URL below is localhost on
//   purpose so an accidental run stays local.
//
// PREREQUISITES
//   - k6 installed (https://k6.io/docs/get-started/installation/)
//   - A reachable, owned OpenMRS instance.
//
// HOW TO RUN
//   k6 run quality/performance/k6/openmrs-smoke.js
//
//   Override target + intensity via environment variables:
//     k6 run \
//       -e BASE_URL=http://localhost:8080/openmrs \
//       -e VUS=5 \
//       -e DURATION=1m \
//       quality/performance/k6/openmrs-smoke.js
//
//   Basic-auth credentials for the authenticated session probe (optional):
//     -e OMRS_USER=admin -e OMRS_PASS=Admin123
//
// EXIT CRITERIA
//   k6 exits non-zero if any threshold below is breached, so this script can
//   gate a CI pipeline (against an owned ephemeral environment only).
// =============================================================================

import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';
import encoding from 'k6/encoding';

// ---------------------------------------------------------------------------
// Configuration (all overridable via -e flags; safe localhost defaults)
// ---------------------------------------------------------------------------
const BASE_URL = (__ENV.BASE_URL || 'http://localhost:8080/openmrs').replace(/\/+$/, '');
const VUS = parseInt(__ENV.VUS || '5', 10);
const DURATION = __ENV.DURATION || '30s';
const OMRS_USER = __ENV.OMRS_USER || 'admin';
const OMRS_PASS = __ENV.OMRS_PASS || 'Admin123';

const FHIR_METADATA = `${BASE_URL}/ws/fhir2/R4/metadata`;
const REST_SESSION = `${BASE_URL}/ws/rest/v1/session`;

// Custom metrics for richer reporting.
const fhirOk = new Rate('fhir_metadata_ok');
const fhirLatency = new Trend('fhir_metadata_latency_ms', true);
const sessionLatency = new Trend('rest_session_latency_ms', true);

// ---------------------------------------------------------------------------
// Test options + thresholds (the pass/fail contract)
// ---------------------------------------------------------------------------
export const options = {
  // A gentle ramp so we never hammer the box.
  scenarios: {
    smoke: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '10s', target: VUS },
        { duration: DURATION, target: VUS },
        { duration: '10s', target: 0 },
      ],
      gracefulRampDown: '10s',
    },
  },
  thresholds: {
    // 99% of all requests must complete quickly; <1% may fail.
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1500', 'p(99)<3000'],
    fhir_metadata_ok: ['rate>0.99'],
    fhir_metadata_latency_ms: ['p(95)<1500'],
    rest_session_latency_ms: ['p(95)<1500'],
  },
  // Keep the summary tidy.
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

// Pre-computed Basic auth header for the authenticated probe.
const authHeader = `Basic ${encoding.b64encode(`${OMRS_USER}:${OMRS_PASS}`)}`;

// ---------------------------------------------------------------------------
// Virtual-user behaviour
// ---------------------------------------------------------------------------
export default function () {
  group('FHIR R4 CapabilityStatement', function () {
    const res = http.get(FHIR_METADATA, {
      headers: { Accept: 'application/fhir+json' },
      tags: { endpoint: 'fhir_metadata' },
    });

    fhirLatency.add(res.timings.duration);

    let fhirVersion = '';
    let resourceType = '';
    try {
      const body = res.json();
      resourceType = body.resourceType || '';
      fhirVersion = body.fhirVersion || '';
    } catch (e) {
      // Non-JSON or empty body — handled by the checks below.
    }

    const passed = check(res, {
      'FHIR metadata HTTP 200': (r) => r.status === 200,
      'is a CapabilityStatement': () => resourceType === 'CapabilityStatement',
      'FHIR version is 4.0.1': () => fhirVersion === '4.0.1',
    });
    fhirOk.add(passed);
  });

  group('REST v1 session', function () {
    const res = http.get(REST_SESSION, {
      headers: { Accept: 'application/json', Authorization: authHeader },
      tags: { endpoint: 'rest_session' },
    });

    sessionLatency.add(res.timings.duration);

    // 200 (authenticated/anonymous session) is the healthy case. 401 still
    // proves the endpoint is alive and enforcing auth, so we accept it as
    // "service reachable" but flag authentication separately.
    check(res, {
      'session endpoint reachable': (r) => r.status === 200 || r.status === 401,
    });
    check(res, {
      'session authenticated': (r) => {
        if (r.status !== 200) return false;
        try {
          return r.json('authenticated') === true;
        } catch (e) {
          return false;
        }
      },
    });
  });

  // Pace each VU iteration so we behave like real users, not a flood.
  sleep(1);
}

// ---------------------------------------------------------------------------
// Custom end-of-test summary (concise, CI-friendly)
// ---------------------------------------------------------------------------
export function handleSummary(data) {
  const m = data.metrics;
  const line = (label, metric, key) => {
    const v = metric && metric.values ? metric.values[key] : undefined;
    return `  ${label.padEnd(34)} ${v === undefined ? 'n/a' : Math.round(v)}`;
  };
  const text =
    '\n=== OpenMRS k6 Smoke Summary ===\n' +
    `  target                             ${BASE_URL}\n` +
    line('http_req_duration p95 (ms)', m.http_req_duration, 'p(95)') +
    '\n' +
    line('http_req_failed rate (%)', m.http_req_failed, 'rate') +
    '\n' +
    line('fhir_metadata_latency p95 (ms)', m.fhir_metadata_latency_ms, 'p(95)') +
    '\n' +
    line('rest_session_latency p95 (ms)', m.rest_session_latency_ms, 'p(95)') +
    '\n================================\n';
  return {
    stdout: text,
    'quality/performance/k6/openmrs-smoke-summary.json': JSON.stringify(data, null, 2),
  };
}
