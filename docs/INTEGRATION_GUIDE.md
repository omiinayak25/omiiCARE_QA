# Integration Guide

> **How omiiCARE_QA talks to external systems.** Every external integration sits
> behind a common adapter interface; in v1.0 every external is stubbed/mocked via
> WireMock, selected by configuration only. This guide lists the integrations, the
> adapter pattern, the automation resource-adapter targets, how to add a new
> integration, and the resilience posture. Adapters are designed in Milestone 1
> and implemented from Milestone 3 (backend) / Milestone 5 (automation). Facts
> defer to [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Ensure that no business or test logic is ever coupled to a concrete external
vendor. A real provider can replace a stub by configuration, never by code change,
preserving testability and the v1.0 "no real integrations" fence.

## Scope

- **In scope:** the integration catalogue, the adapter pattern, configuration-
  driven endpoint selection, the automation resource-adapter targets, the
  "add a new integration" procedure, and failure/retry/circuit-breaker notes.
- **Out of scope:** real production credentials/contracts (none in v1.0 — see
  [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3) and the
  full resilience implementation (Milestone 7).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Integration Engineer (M3) | Implement backend adapters + WireMock stubs |
| QA Architect (M5) | Implement automation resource adapters + environment switching |
| SRE/DevOps (M2/M7) | Provide WireMock service and resilience configuration |

---

## 1. Integration Catalogue

| Integration | v1.0 treatment | Adapter port (illustrative) |
|-------------|----------------|-----------------------------|
| Insurance eligibility/claims | WireMock stub | `InsuranceGatewayPort` |
| Payment gateway | WireMock stub | `PaymentGatewayPort` |
| SMS provider | WireMock stub (+ MailHog-style sink) | `SmsSenderPort` |
| Email provider | MailHog (dev) / WireMock stub | `EmailSenderPort` |
| Identity provider | Keycloak (containerized) | `IdentityProviderPort` |
| Government/national registry | WireMock stub | `RegistryLookupPort` |
| Lab equipment / LIS | WireMock + HL7 stub | `LabDevicePort` |
| FHIR servers | HAPI FHIR (containerized) / stub | `FhirServerPort` |
| HL7 interfaces | WireMock + sample messages | `Hl7InterfacePort` |

All ports are defined in the application layer; concrete adapters live in the
infrastructure layer (see [ARCHITECTURE.md](../ARCHITECTURE.md) §4).

## 2. Adapter Pattern

```
   Application use case
          │  depends on
          ▼
   ┌──────────────────┐      implemented by      ┌───────────────────────┐
   │  <<port>>        │ ◀─────────────────────── │  WireMockAdapter (v1.0)│
   │  PaymentGateway  │ ◀─────────────────────── │  RealVendorAdapter (fut)│
   └──────────────────┘                          └───────────────────────┘
```

- The **port** is a stable interface owned by the application/domain; it speaks the
  domain's language, not a vendor's API.
- An **adapter** implements the port for one concrete target (WireMock stub today,
  a real vendor later). Swapping adapters never touches callers.
- This is the Ports & Adapters (Hexagonal) pattern, consistent with the Clean
  Architecture layering in [ARCHITECTURE.md](../ARCHITECTURE.md) §4.

## 3. v1.0 = Stubbed / Mocked (WireMock)

- Every external is **stubbed with WireMock** (containerized via Docker Compose,
  Milestone 2). Stubs return deterministic, synthetic, PHI-safe responses.
- Stubs model success, validation failure, denial, timeout, and error responses so
  that adapter behaviour and resilience can be tested without a real vendor.
- No real credentials, no real PHI, no live external calls leave the stack
  (fence: [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3).

## 4. Configuration-Driven Endpoint Selection

- Each port has a configured implementation key and base URL, resolved per Spring
  profile (no code branching):

```yaml
omiicare:
  integrations:
    payment:
      adapter: wiremock          # wiremock | stripe-like | ...
      base-url: http://wiremock:8080/payment
    insurance:
      adapter: wiremock
      base-url: http://wiremock:8080/insurance
    fhir:
      adapter: hapi
      base-url: http://hapi-fhir:8080/fhir
```

- Switching `dev → docker → qa` changes URLs/adapters by configuration only,
  consistent with the platform's profile-driven philosophy
  ([ARCHITECTURE.md](../ARCHITECTURE.md) §1, §8).

## 5. Automation Resource-Adapter Targets

The Milestone 5 automation platform applies the same adapter principle to its
**System-Under-Test targets** (see [ARCHITECTURE.md](../ARCHITECTURE.md) §6):

| Target | Nature | Use in QA |
|--------|--------|-----------|
| Local omiiCARE | The platform's own backend | Primary SUT |
| OpenMRS | Open-source EMR | Cross-system API/data parity tests |
| OpenEMR | Open-source EHR | Interoperability scenarios |
| HAPI FHIR | Reference FHIR R4 server | FHIR conformance/contract tests |
| SMART Health IT | SMART-on-FHIR sandbox | App-launch/FHIR scenarios |
| OpenFDA | Public drug/device data API | Reference-data lookups |
| DummyJSON | Generic REST mock | Framework smoke / negative tests |
| Restful Booker | Booking REST sandbox | Scheduling-style API patterns |

Tests call the common adapter interface; the active target is selected by
configuration, so switching targets requires no test-code change.

## 6. Adding a New Integration

1. **Define/confirm the port** in the application layer (domain language only).
2. **Write the adapter** in infrastructure implementing that port for the new
   target; no caller changes.
3. **Add configuration** (`adapter` key + `base-url`) per profile; default to a
   WireMock stub for non-production profiles.
4. **Author a WireMock stub** (success + failure + edge responses) for tests.
5. **Add contract tests** asserting the adapter satisfies the port contract.
6. **Document** the integration here and update the catalogue (§1).

No existing caller or test is modified — adding an integration is adding an
adapter and configuration, nothing more.

## 7. Failure, Retry & Circuit-Breaker Notes

> Full resilience is a Milestone 7 deliverable; v1.0 seams it in.

| Concern | v1.0 seam | M7 implementation |
|---------|-----------|-------------------|
| Timeouts | Per-adapter connect/read timeouts configured | Tuned per integration |
| Retries | Idempotent calls retried with capped backoff + jitter; idempotency keys honoured (see [API_BLUEPRINT.md](API_BLUEPRINT.md) §5) | Resilience4j retry policies |
| Circuit breaker | Documented breaker around each adapter; open on sustained failure, half-open probe | Resilience4j circuit breakers |
| Bulkhead | Adapter calls isolated so one slow external cannot exhaust shared threads | Resilience4j bulkheads |
| Fallback | Defined per integration (queue, degrade, or fail-fast with Problem Details) | Wired and tested |
| Observability | Correlation/trace IDs propagated into adapter calls (OpenTelemetry) | Dashboards/alerts (Prometheus/Grafana) |

## Examples

- *Switching the FHIR target:* set `omiicare.integrations.fhir.adapter` from
  `hapi` to a stub and change `base-url`; callers and tests are untouched.
- *Simulating a payment timeout:* point the payment WireMock stub at a delayed
  response to verify the adapter's timeout, retry, and circuit-breaker behaviour.

## Future Enhancements

- Real vendor adapters (payment, insurance, SMS/email) — post-1.0, behind the same
  ports (see [ROADMAP.md](../ROADMAP.md)).
- Async/event-driven integration via the Outbox seam ([ARCHITECTURE.md](../ARCHITECTURE.md) §7).
- Contract-test pacts published per integration.

## Dependencies

- Layering & adapter principle: [ARCHITECTURE.md](../ARCHITECTURE.md) §4, §6.
- HL7/FHIR stubs: [HL7_GUIDE.md](HL7_GUIDE.md), [FHIR_GUIDE.md](FHIR_GUIDE.md).
- Idempotency/error contract: [API_BLUEPRINT.md](API_BLUEPRINT.md).
- Infra (WireMock/Keycloak/HAPI): [PROJECT_METADATA.md](PROJECT_METADATA.md) §3.

## References

- WireMock documentation; Resilience4j; Keycloak; HAPI FHIR.
- Ports & Adapters (Hexagonal Architecture, Cockburn).
- [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3 Out of scope.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Senior Backend Engineer | Initial external-integration adapter guide (Milestone 1) |
