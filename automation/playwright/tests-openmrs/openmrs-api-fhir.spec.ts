import { test, expect } from '@playwright/test';

/**
 * OpenMRS REST + FHIR R4 API tests against the live public demo (read-only).
 * Every assertion is grounded in verified live behavior of o2.openmrs.org.
 * Endpoints: REST /openmrs/ws/rest/v1/*, FHIR R4 /openmrs/ws/fhir2/R4/*.
 */

const ROOT = (process.env.OPENMRS_BASE_URL ?? 'https://o2.openmrs.org') + '/openmrs';
const basic = (u: string, p: string) => ({ Authorization: 'Basic ' + Buffer.from(`${u}:${p}`).toString('base64') });
const AUTH = basic('admin', 'Admin123');
const BAD = basic('admin', 'wrong-password');
const FHIR = { Accept: 'application/fhir+json' };

test.describe('OpenMRS · FHIR R4 API (read-only)', () => {
  test('FHIR-01 · metadata returns a public R4 CapabilityStatement (HAPI, 4.0.1)', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/fhir2/R4/metadata`, { headers: FHIR });
    expect(r.status()).toBe(200);
    expect(r.headers()['content-type']).toContain('fhir+json');
    const b = await r.json();
    expect(b.resourceType).toBe('CapabilityStatement');
    expect(b.fhirVersion).toBe('4.0.1');
    expect(b.status).toBe('active');
  });

  test('FHIR-02 · Patient search WITHOUT auth is rejected (401)', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/fhir2/R4/Patient?_count=1`, { headers: FHIR });
    expect(r.status()).toBe(401);
  });

  test('FHIR-03 · Patient search WITH auth returns a searchset Bundle', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/fhir2/R4/Patient?_count=3`, { headers: { ...AUTH, ...FHIR } });
    expect(r.status()).toBe(200);
    const b = await r.json();
    expect(b.resourceType).toBe('Bundle');
    expect(b.type).toBe('searchset');
    expect(b.total).toBeGreaterThan(0);
    expect(Array.isArray(b.entry)).toBeTruthy();
    expect(b.entry[0].resource.resourceType).toBe('Patient');
  });

  test('FHIR-04 · Patient read by id returns that Patient resource', async ({ request }) => {
    const s = await request.get(`${ROOT}/ws/fhir2/R4/Patient?_count=1`, { headers: { ...AUTH, ...FHIR } });
    const id = (await s.json()).entry[0].resource.id as string;
    const r = await request.get(`${ROOT}/ws/fhir2/R4/Patient/${id}`, { headers: { ...AUTH, ...FHIR } });
    expect(r.status()).toBe(200);
    const b = await r.json();
    expect(b.resourceType).toBe('Patient');
    expect(b.id).toBe(id);
  });

  test('FHIR-05 · Patient search with BAD credentials is rejected (401)', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/fhir2/R4/Patient?_count=1`, { headers: { ...BAD, ...FHIR } });
    expect(r.status()).toBe(401);
  });

  test('FHIR-06 · unknown Patient id returns 404 (negative)', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/fhir2/R4/Patient/does-not-exist-uuid-0000`, { headers: { ...AUTH, ...FHIR } });
    expect(r.status()).toBe(404);
  });
});

test.describe('OpenMRS · REST API (read-only)', () => {
  test('REST-01 · session without auth reports authenticated=false', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/rest/v1/session`);
    expect(r.status()).toBe(200);
    expect((await r.json()).authenticated).toBe(false);
  });

  test('REST-02 · session with valid auth reports authenticated admin', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/rest/v1/session`, { headers: AUTH });
    expect(r.status()).toBe(200);
    const b = await r.json();
    expect(b.authenticated).toBe(true);
    expect(b.user?.username).toBe('admin');
  });

  test('REST-03 · patient search returns a results array', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/rest/v1/patient?q=Test&limit=2`, { headers: AUTH });
    expect(r.status()).toBe(200);
    expect(await r.json()).toHaveProperty('results');
  });

  test('REST-04 · protected /user without auth is rejected (401)', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/rest/v1/user?limit=1`);
    expect(r.status()).toBe(401);
  });

  test('REST-05 · protected /user with BAD credentials is rejected (401)', async ({ request }) => {
    const r = await request.get(`${ROOT}/ws/rest/v1/user?limit=1`, { headers: BAD });
    expect(r.status()).toBe(401);
  });
});
