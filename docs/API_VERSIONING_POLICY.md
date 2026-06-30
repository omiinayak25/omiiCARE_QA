# API Versioning Policy

> **The contract for how omiiCARE_QA evolves its HTTP API without breaking
> clients.** It defines the versioning scheme, what counts as breaking vs
> non-breaking, deprecation and sunset timelines, the signalling headers, and the
> relationship to Semantic Versioning. Designed in Milestone 1; enforced from the
> first endpoint in Milestone 3. Facts defer to
> [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Guarantee that API consumers (frontend M4, automation M5, external adapters) can
depend on a stable contract, know in advance when and how it will change, and
migrate on a predictable, communicated timeline.

## Scope

- **In scope:** URI versioning scheme, change classification, deprecation/sunset
  policy, compatibility guarantees, signalling headers, client migration
  guidance, and the link to product-level SemVer.
- **Out of scope:** the endpoint catalogue itself (see
  [API_BLUEPRINT.md](API_BLUEPRINT.md)) and FHIR/HL7 versioning (those track their
  own standards).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| API/Backend Engineer | Classify each change; add deprecation headers/docs |
| Maintainer | Approve breaking changes and sunset dates |
| QA Architect | Contract tests assert backward compatibility within a major version |
| Technical Writer | Publish migration guides and changelog entries |

---

## 1. Versioning Scheme

- The API uses **URI versioning**: the major version is the first path segment
  after the base — **`/api/v1/`**. The next incompatible generation is
  **`/api/v2/`**, served side-by-side during migration.
- **Only the major version appears in the URI.** Minor, backward-compatible
  growth (new endpoints, new optional fields) happens *within* `v1` without a URI
  change.
- The running build also exposes its product version via
  `GET /api/v1/system/version` and the `X-API-Version` response header
  (e.g. `1.4.0`), tying the live surface back to SemVer (§7).

| Aspect | Choice | Reason |
|--------|--------|--------|
| Location | URI path (`/api/v{major}/`) | Explicit, cache-friendly, trivially routable, obvious in logs |
| Granularity | Major only in URI | Avoids version churn for additive changes |
| Coexistence | `v1` and `v2` run together during a migration window | Lets clients migrate gradually |

## 2. Breaking vs Non-Breaking Changes

| Non-breaking (allowed within a major version) | Breaking (requires a new major version) |
|-----------------------------------------------|-----------------------------------------|
| Adding a new endpoint or resource | Removing or renaming an endpoint/resource |
| Adding an **optional** request field | Adding a **required** request field |
| Adding a new field to a response | Removing/renaming a response field |
| Adding a new enum value the client may ignore | Changing the type/format of an existing field |
| Adding a new optional query parameter | Changing default behaviour of an existing parameter |
| Relaxing a validation constraint | Tightening a validation constraint clients relied on |
| Adding a new error `code` for a new condition | Changing the HTTP status of an existing condition |
| Adding response headers | Changing auth scheme or required headers |

Clients MUST be tolerant readers (ignore unknown fields) so that non-breaking
additions never require client changes.

## 3. Backward-Compatibility Guarantees

- Within a major version (`v1.x`), the API is **additive-only**; no documented
  endpoint, field, status, or error `code` is removed or repurposed.
- Existing `code` values in Problem Details responses (see
  [API_BLUEPRINT.md](API_BLUEPRINT.md) §3) are stable identifiers and are never
  reused for a different meaning.
- Pagination, sorting, and filtering parameter semantics are stable within a
  major version.
- Contract tests (M7) fail the build if a change would break a `v1` consumer.

## 4. Deprecation Policy & Timelines

When an element of `v1` must be retired, it follows a staged lifecycle rather than
disappearing:

| Stage | Action | Minimum duration |
|-------|--------|------------------|
| Announce | Mark deprecated in OpenAPI (`deprecated: true`), changelog, and migration guide | — |
| Signal | Emit `Deprecation` and `Sunset` headers on affected responses | from announcement |
| Grace period | Endpoint keeps working unchanged | **≥ 180 days** for external-facing surfaces |
| Sunset | Element removed only in the next **major** version, never within `v1` | at/after the `Sunset` date |

A breaking removal never lands inside `v1`; it lands in `v2`, with `v1`
remaining available throughout the announced support window.

## 5. Sunset Policy & Communication Plan

| Channel | Content |
|---------|---------|
| OpenAPI/Swagger | `deprecated: true`, description noting replacement and sunset date |
| Response headers | `Deprecation` and `Sunset` (§6) on every affected response |
| [CHANGELOG.md](../CHANGELOG.md) | Entry under the release that introduces the deprecation and the one that sunsets it |
| Migration guide | `docs/api/migrations/v1-to-v2.md` (created when `v2` is planned) |
| Release notes | Deprecations and removals called out per version |

A whole major version is supported for **at least 12 months** after its successor
(`vN+1`) is released, then sunset on a published date.

## 6. Header Signalling

| Header | Example | Meaning |
|--------|---------|---------|
| `Deprecation` | `Deprecation: Tue, 30 Jun 2026 00:00:00 GMT` | This endpoint/version is deprecated as of the given date ([draft RFC](https://datatracker.ietf.org/doc/draft-ietf-httpapi-deprecation-header/)) |
| `Sunset` | `Sunset: Sun, 27 Dec 2026 00:00:00 GMT` | Date after which the endpoint may stop working ([RFC 8594](https://www.rfc-editor.org/rfc/rfc8594)) |
| `Link` | `Link: </api/v2/patients>; rel="successor-version"` | Points to the replacement |
| `Warning` | `Warning: 299 - "Deprecated API; migrate to v2 by 2026-12-27"` | Human-readable advisory |
| `X-API-Version` | `X-API-Version: 1.4.0` | The serving build's SemVer |

## 7. Relationship to SemVer

The API major in the URI maps to the product's [Semantic Versioning](../VERSIONING.md)
major, but they are not identical knobs:

| SemVer change | API effect |
|---------------|------------|
| **MAJOR** (`1.x → 2.0`) | May introduce `/api/v2/`; `v1` enters its deprecation/sunset window |
| **MINOR** (`1.3 → 1.4`) | Backward-compatible API additions within `/api/v1/`; URI unchanged |
| **PATCH** (`1.4.0 → 1.4.1`) | Bug fixes, no contract change |

During the `0.x` pre-release line (current — see
[PROJECT_METADATA.md](PROJECT_METADATA.md) §7), breaking API changes are permitted
without a URI bump; the `v1` stability guarantees in this policy take full effect
from the `1.0.0` release.

## 8. Version Lifecycle Table

| API version | Status | Introduced | Deprecated | Sunset |
|-------------|--------|-----------|------------|--------|
| `v1` | 🟦 Active (pre-1.0 hardening) | M3 | — | — |
| `v2` | ⬜ Planned | Post-1.0 (roadmap) | — | — |

Legend: ✅ Stable · 🟦 Active · ⬜ Planned · ⚠️ Deprecated · ⛔ Sunset.

## 9. Client Migration Guidance

- **Be a tolerant reader:** ignore unknown JSON fields and unknown enum values so
  additive changes never break you.
- **Pin the major, not the minor:** call `/api/v1/`; do not hard-code behaviours
  that the policy lists as non-breaking-changeable.
- **Watch the signals:** treat `Deprecation`/`Sunset` headers as actionable; log
  and alert on them in client code.
- **Read the migration guide** for any `vN → vN+1` move; it lists field-level
  mappings and removed elements.
- **Test against both versions** during a migration window using the automation
  adapter layer (see [ARCHITECTURE.md](../ARCHITECTURE.md) §6).

## Examples

- Adding `middleName` (optional) to the patient response is a **minor** change —
  same `/api/v1/patients`, no client action required.
- Renaming `startAt` to `scheduledStart` is **breaking** — it appears only in
  `/api/v2/`; `v1` keeps `startAt` until its published sunset.
- A consumer receiving `Sunset: Sun, 27 Dec 2026 …` on `/api/v1/lab-orders` plans
  its migration to the successor named in the `Link` header before that date.

## Future Enhancements

- Automated detection of breaking changes in CI by diffing the committed OpenAPI
  spec between releases (M8).
- A consumer-facing deprecations dashboard fed by emitted headers.

## Dependencies

- Product versioning anchor: [VERSIONING.md](../VERSIONING.md) and
  [PROJECT_METADATA.md](PROJECT_METADATA.md) §7.
- The surface being versioned: [API_BLUEPRINT.md](API_BLUEPRINT.md).
- Change log of versions: [CHANGELOG.md](../CHANGELOG.md).

## References

- [RFC 8594](https://www.rfc-editor.org/rfc/rfc8594) — the `Sunset` HTTP header.
- IETF `Deprecation` header draft.
- [Semantic Versioning 2.0.0](https://semver.org/).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Senior API Engineer | Initial API versioning & deprecation policy (Milestone 1) |
