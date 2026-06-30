// =============================================================================
// omiiCARE QA — k6 Load Test: Auth Login + Patient List
// -----------------------------------------------------------------------------
// PERFORMANCE SAFETY RULE: This test targets ONLY local / Docker / owned
// infrastructure (default http://localhost:8080). NEVER point BASE_URL at a
// public website or any system you do not own and have explicit permission to
// load-test.
// -----------------------------------------------------------------------------
// Scenario:
//   1. Ramping VUs (load profile) authenticate via /api/v1/auth/login.
//   2. Each VU lists patients via /api/v1/patients using the bearer token.
//   3. Thresholds gate the run: p95 latency < 500ms, error rate < 1%.
//
// Run:
//   k6 run quality/performance/k6/login-load.js
//   BASE_URL=http://localhost:8080 USERNAME=demo.admin PASSWORD='Admin@12345' \
//     k6 run quality/performance/k6/login-load.js
// =============================================================================

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

// ---------------------------------------------------------------------------
// Configuration (override via environment variables: -e KEY=value)
// ---------------------------------------------------------------------------
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const USERNAME = __ENV.USERNAME || 'demo.admin';
const PASSWORD = __ENV.PASSWORD || 'Admin@12345';

// ---------------------------------------------------------------------------
// Custom metrics
// ---------------------------------------------------------------------------
const loginDuration = new Trend('login_duration', true);
const patientsDuration = new Trend('patients_list_duration', true);
const loginFailRate = new Rate('login_failed');
const businessErrors = new Counter('business_errors');

// ---------------------------------------------------------------------------
// Test options — ramping VU load profile + thresholds
// ---------------------------------------------------------------------------
export const options = {
  scenarios: {
    login_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 10 }, // ramp up to 10 VUs
        { duration: '1m', target: 25 },  // ramp up to 25 VUs
        { duration: '2m', target: 25 },  // sustain peak load (steady state)
        { duration: '30s', target: 0 },  // ramp down / cooldown
      ],
      gracefulRampDown: '15s',
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
    login_failed: ['rate<0.01'],
    login_duration: ['p(95)<600'],
    patients_list_duration: ['p(95)<500'],
  },
};

// ---------------------------------------------------------------------------
// Authenticate and return a bearer token (or null on failure)
// ---------------------------------------------------------------------------
function login() {
  const payload = JSON.stringify({ username: USERNAME, password: PASSWORD });
  const params = {
    headers: { 'Content-Type': 'application/json' },
    tags: { name: 'POST /api/v1/auth/login' },
  };

  const res = http.post(`${BASE_URL}/api/v1/auth/login`, payload, params);
  loginDuration.add(res.timings.duration);

  const ok = check(res, {
    'login status is 200': (r) => r.status === 200,
    'login returns a token': (r) => {
      try {
        const body = r.json();
        return Boolean(body && (body.token || body.accessToken));
      } catch (e) {
        return false;
      }
    },
  });

  loginFailRate.add(!ok);
  if (!ok) {
    businessErrors.add(1);
    return null;
  }

  const body = res.json();
  return body.token || body.accessToken;
}

// ---------------------------------------------------------------------------
// Main VU iteration
// ---------------------------------------------------------------------------
export default function () {
  let token;

  group('authenticate', function () {
    token = login();
  });

  if (!token) {
    sleep(1);
    return;
  }

  group('list patients', function () {
    const params = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'application/json',
      },
      tags: { name: 'GET /api/v1/patients' },
    };

    const res = http.get(`${BASE_URL}/api/v1/patients?page=0&size=20`, params);
    patientsDuration.add(res.timings.duration);

    const ok = check(res, {
      'patients status is 200': (r) => r.status === 200,
      'patients body is parseable': (r) => {
        try {
          r.json();
          return true;
        } catch (e) {
          return false;
        }
      },
    });

    if (!ok) {
      businessErrors.add(1);
    }
  });

  sleep(Math.random() * 2 + 1); // think time: 1-3s
}

// ---------------------------------------------------------------------------
// Lifecycle: emit a one-line banner so reports are self-describing
// ---------------------------------------------------------------------------
export function setup() {
  console.log(`[k6] omiiCARE login-load — target=${BASE_URL} (owned infra only)`);
  return { startedAt: new Date().toISOString() };
}
