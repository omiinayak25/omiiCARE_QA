// =============================================================================
// omiiCARE QA — k6 Stress & Spike Test: Appointment Booking
// -----------------------------------------------------------------------------
// PERFORMANCE SAFETY RULE: Targets ONLY local / Docker / owned infrastructure
// (default http://localhost:8080). NEVER run against a public website or any
// system you do not own. Stress profiles intentionally push the SUT past its
// comfort zone — only do this to infrastructure you control.
// -----------------------------------------------------------------------------
// Profile combines a STRESS ramp (find the breaking point) with a SPIKE
// (sudden surge) to exercise appointment booking under burst conditions.
//
// Run:
//   k6 run quality/performance/k6/appointment-stress.js
//   BASE_URL=http://localhost:8080 k6 run quality/performance/k6/appointment-stress.js
// =============================================================================

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const USERNAME = __ENV.USERNAME || 'demo.admin';
const PASSWORD = __ENV.PASSWORD || 'Admin@12345';

// Custom metrics
const bookingDuration = new Trend('appointment_booking_duration', true);
const bookingFailRate = new Rate('appointment_booking_failed');
const bookingCount = new Counter('appointments_booked');

export const options = {
  scenarios: {
    // --- STRESS: progressively heavier load to find the saturation point ----
    stress_ramp: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '1m', target: 20 },   // warm up
        { duration: '2m', target: 50 },   // increase load
        { duration: '2m', target: 100 },  // heavy load
        { duration: '2m', target: 150 },  // stress — beyond expected peak
        { duration: '1m', target: 0 },    // recover
      ],
      gracefulRampDown: '20s',
    },
    // --- SPIKE: sudden surge layered on top, starts after the ramp warms up -
    traffic_spike: {
      executor: 'ramping-vus',
      startVUs: 0,
      startTime: '4m',
      stages: [
        { duration: '10s', target: 200 }, // instantaneous surge
        { duration: '30s', target: 200 }, // hold the spike
        { duration: '10s', target: 0 },   // drop
      ],
      gracefulRampDown: '10s',
    },
  },
  thresholds: {
    // Stress tests tolerate degradation but must not collapse entirely.
    http_req_failed: ['rate<0.10'],            // < 10% errors under stress
    appointment_booking_failed: ['rate<0.10'],
    http_req_duration: ['p(95)<2000'],         // p95 under 2s even when stressed
  },
};

function login() {
  const res = http.post(
    `${BASE_URL}/api/v1/auth/login`,
    JSON.stringify({ username: USERNAME, password: PASSWORD }),
    { headers: { 'Content-Type': 'application/json' }, tags: { name: 'POST /api/v1/auth/login' } },
  );
  if (res.status !== 200) {
    return null;
  }
  try {
    const body = res.json();
    return body.token || body.accessToken;
  } catch (e) {
    return null;
  }
}

function isoOffsetDays(days) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  d.setHours(10, 0, 0, 0);
  return d.toISOString();
}

export default function () {
  const token = login();
  if (!token) {
    bookingFailRate.add(true);
    sleep(1);
    return;
  }

  const authHeaders = {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  group('book appointment', function () {
    const start = isoOffsetDays(__VU % 30 + 1);
    const payload = JSON.stringify({
      patientId: ((__VU * 31 + __ITER) % 500) + 1,
      providerId: (__VU % 10) + 1,
      startTime: start,
      reason: 'Load test appointment (owned infra only)',
    });

    const res = http.post(`${BASE_URL}/api/v1/appointments`, payload, {
      headers: authHeaders,
      tags: { name: 'POST /api/v1/appointments' },
    });

    bookingDuration.add(res.timings.duration);

    // Accept 200/201 as success; 409 (conflict/slot taken) is a valid
    // business outcome under contention, not a server failure.
    const success = check(res, {
      'booking accepted (200/201/409)': (r) =>
        r.status === 200 || r.status === 201 || r.status === 409,
    });

    bookingFailRate.add(!success);
    if (res.status === 200 || res.status === 201) {
      bookingCount.add(1);
    }
  });

  sleep(Math.random() + 0.5); // short think time to keep pressure up
}

export function setup() {
  console.log(`[k6] omiiCARE appointment-stress — target=${BASE_URL} (owned infra only)`);
}
