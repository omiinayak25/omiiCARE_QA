# AI-QE Reference Tools (`ai/qe/`)

Runnable **Python 3, standard-library only, offline** reference scripts that turn
the existing omiiCARE_QA artifacts (RTM, requirements catalog, test-case export,
Surefire/Playwright results, bug template) into actionable QE outputs.

> Design intent: these are *reference / glue* tools for an AI-assisted QE
> workflow. The single "AI" seam (`_common.ai_complete`) is a **clearly-marked,
> deterministic stub** by default — no network, no credentials, zero cost. Swap
> in a real model when you want richer drafts (see [Plugging in a model](#plugging-in-a-real-model)).

## Why std-lib + offline

- **Zero install / zero cost** — runs on Python 3.8+ with nothing but the stdlib,
  on any dev box or CI runner (Python 3 per repo tooling).
- **PHI-safe** — reads only test metadata and synthetic IDs; no patient data.
- **CI-friendly** — gap/triage tools return non-zero on findings to act as gates.

## Requirements

- Python 3.8+ (no `pip install` needed).
- Run from anywhere — repo root is auto-discovered (markers: `manual-testing/`,
  `docs/`, `automation/`). Override with `--root` if needed.

## Tools

| Script | Reads (real repo paths) | Produces |
|--------|-------------------------|----------|
| `coverage_gap_analysis.py` | `manual-testing/rtm/RTM.csv`, `docs/requirements/requirements-catalog.md` | Requirements/modules with risk-based **thin coverage**; module roll-up |
| `regression_impact.py` | `manual-testing/rtm/RTM.csv`, `manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv` | Changed paths/modules → **impacted TC IDs + requirements** (the regression slice) |
| `failure_triage.py` | `automation/target/surefire-reports/*.xml`, `apps/backend/target/surefire-reports/*.xml`, `automation/playwright/results*/results.json` | **Grouped failures** by signature + RCA stubs |
| `bug_report_drafter.py` | a failure record (JSON) + `manual-testing/bug-templates/BUG_REPORT_TEMPLATE.md` (structure) | A **filled `OMII-BUG-NNNN`** Markdown defect |
| `_common.py` | — | Shared helpers + the pluggable `ai_complete` seam |

## Quick start

```bash
# 1) Where is coverage thin? (risk-based: High>=3, Medium>=2, Low>=1 tests)
python3 ai/qe/coverage_gap_analysis.py --format md
python3 ai/qe/coverage_gap_analysis.py --module AUTH --explain

# 2) What must re-run after a change?
python3 ai/qe/regression_impact.py \
    --paths apps/backend/.../RegistrationController.java apps/frontend/src/Appointment.tsx
python3 ai/qe/regression_impact.py --modules FHIR,AUTH --json
python3 ai/qe/regression_impact.py --git-range HEAD~1..HEAD   # uses `git diff`

# 3) Triage a test run (auto-discovers Surefire + Playwright results)
mvn -pl automation test            # 98 unit tests (default build)
python3 ai/qe/failure_triage.py --rca

# 4) Turn failures into draft defects, end to end
python3 ai/qe/failure_triage.py --json \
  | python3 ai/qe/bug_report_drafter.py --from-triage \
        --build "1.0.0 / develop@SHA" --out drafts.md
```

### Pipeline

```
failure_triage.py --json  ──►  bug_report_drafter.py --from-triage  ──►  OMII-BUG-*.md
        ▲                                                                    │
   Surefire XML / Playwright JSON                          BUG_REPORT_TEMPLATE.md (structure)
```

## Exit codes (for CI gating)

| Code | Meaning |
|------|---------|
| `0` | Success / no findings (or `--soft` used) |
| `1` | Bad input / missing artifact |
| `2` | Findings present (coverage gap above threshold, or test failures) |

`coverage_gap_analysis.py` and `failure_triage.py` accept `--soft` to always
exit `0` (report-only mode).

## Key options

- `coverage_gap_analysis.py`: `--module`, `--format text|md`, `--json`,
  `--explain`, `--top N`, `--high-only DEFICIT`, `--soft`.
- `regression_impact.py`: `--paths`, `--modules`, `--git-range`, `--explain`,
  `--json`, `--format`. Path→module keywords live in `PATH_KEYWORD_MAP`
  (values are **real** module tokens such as `AUTH`, `APPT`, `REG`, `FHIR`,
  `PORTAL`); extend it as the SUT grows.
- `failure_triage.py`: positional result files/dirs (default auto-discover),
  `--rca`, `--json`, `--format`, `--soft`.
- `bug_report_drafter.py`: `--json-file`, `--from-triage`, `--start-no`,
  `--build`, `--env`, `--reporter`, `--cycle`, `--out`.

## Plugging in a real model

The only AI seam is `ai_complete()` in `_common.py`. By default
`OMII_QE_AI_BACKEND=stub` returns a deterministic, labelled draft offline.

To use a live model:

1. Implement `_dispatch(backend, prompt, system, max_tokens)` in `_common.py`
   (e.g. a thin HTTP client for your provider).
2. Export `OMII_QE_AI_BACKEND=<your-backend>` before running a tool.

Recommended default for these classification/summarization tasks: a small,
fast model (e.g. **Anthropic Claude Haiku**) per the repo cost rules. Keep all
inputs **synthetic / PHI-safe** — never send patient data to any model. *(A live
backend implementation is left as a pluggable extension — planned.)*

## Notes & guardrails

- These tools only **read** artifacts and **write** reports/drafts; they never
  touch source, `pom.xml`, or build files.
- Performance/security checks live elsewhere (`quality/performance`,
  `quality/security`) and run only on owned/local environments — never against
  `o2.openmrs.org`.
- `requirements-catalog.md` is cross-checked for a total-count sanity delta; the
  RTM remains the authoritative REQ→TC source.
