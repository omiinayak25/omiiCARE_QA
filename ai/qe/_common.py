"""
Shared helpers for the omiiCARE_QA AI-QE reference tools (ai/qe/).

Standard-library only (Python 3.8+). No third-party deps, no network.
Every tool in this package imports from here so that repo-root discovery,
CSV parsing, and the pluggable AI stub stay consistent.

The "AI" entrypoint (`ai_complete`) is a clearly-marked, deterministic
stub by default. It is the single seam where a real LLM call would be
wired in. See ai/qe/README.md for how to plug in a provider.
"""
from __future__ import annotations

import csv
import os
import sys
from pathlib import Path
from typing import Dict, Iterable, List, Optional


# --------------------------------------------------------------------------
# Repo-root discovery
# --------------------------------------------------------------------------
# We locate the repository root by walking up from this file until we find a
# marker we know exists in omiiCARE_QA. This keeps the tools runnable from any
# working directory (CI, IDE, or a developer shell).
_ROOT_MARKERS = ("manual-testing", "docs", "automation")


def repo_root(start: Optional[Path] = None) -> Path:
    here = (start or Path(__file__)).resolve()
    for parent in [here, *here.parents]:
        if all((parent / m).is_dir() for m in _ROOT_MARKERS):
            return parent
    # Fallback: ai/qe/_common.py -> ai/qe -> ai -> <root>
    return Path(__file__).resolve().parents[2]


def resolve(path_rel: str, root: Optional[Path] = None) -> Path:
    return (root or repo_root()) / path_rel


# --------------------------------------------------------------------------
# Known real repo artifacts (verified to exist in omiiCARE_QA)
# --------------------------------------------------------------------------
RTM_CSV = "manual-testing/rtm/RTM.csv"
ALL_TEST_CASES_CSV = "manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv"
REQUIREMENTS_CATALOG = "docs/requirements/requirements-catalog.md"
BUG_TEMPLATE = "manual-testing/bug-templates/BUG_REPORT_TEMPLATE.md"
SUREFIRE_DIR = "automation/target/surefire-reports"
BACKEND_SUREFIRE_DIR = "apps/backend/target/surefire-reports"
PLAYWRIGHT_OPENMRS_JSON = "automation/playwright/results-openmrs/results.json"
PLAYWRIGHT_OMIICARE_JSON = "automation/playwright/results/results.json"


# --------------------------------------------------------------------------
# CSV helpers
# --------------------------------------------------------------------------
def read_csv_dicts(path: Path) -> List[Dict[str, str]]:
    """Read a quoted CSV into a list of dicts. Tolerant of BOM."""
    if not path.is_file():
        raise FileNotFoundError(f"Expected artifact not found: {path}")
    with path.open(newline="", encoding="utf-8-sig") as fh:
        return [dict(row) for row in csv.DictReader(fh)]


def split_ids(raw: str, sep: str = ";") -> List[str]:
    """Split a 'TC-A; TC-B' style cell into clean tokens."""
    if not raw:
        return []
    return [tok.strip() for tok in raw.split(sep) if tok.strip()]


def tc_prefix(tc_id: str) -> str:
    """'TC-AUTH-0001' -> 'AUTH' ; 'REQ-AUTH-001' -> 'AUTH'.

    Extracts the module token (the middle segment) used across the repo's
    ID scheme. Returns '' if the id does not match the expected shape.
    """
    parts = tc_id.split("-")
    if len(parts) >= 3:
        return parts[1].upper()
    return ""


# --------------------------------------------------------------------------
# Pluggable AI seam (STUB by default)
# --------------------------------------------------------------------------
# This is intentionally NOT a network call. It is a deterministic,
# offline-friendly placeholder so the reference tools run anywhere with zero
# cost and zero credentials. To use a real model, replace the body of
# `ai_complete` (or set OMII_QE_AI_BACKEND and implement `_dispatch`).
#
# Recommended default when wiring a real provider: Anthropic Claude Haiku for
# these classification/summarization tasks (cheap, fast), per the repo cost
# rules. Keep prompts PHI-safe: only synthetic data may be sent.
def ai_complete(prompt: str, *, system: str = "", max_tokens: int = 512) -> str:
    """Pluggable completion seam.

    Default backend is 'stub': returns a clearly-labelled, rule-based draft
    so downstream output is useful without any API call. Selectable via the
    OMII_QE_AI_BACKEND env var (default 'stub').
    """
    backend = os.environ.get("OMII_QE_AI_BACKEND", "stub").lower()
    if backend == "stub":
        return _stub_complete(prompt, system=system)
    return _dispatch(backend, prompt, system=system, max_tokens=max_tokens)


def _stub_complete(prompt: str, *, system: str = "") -> str:
    """Deterministic offline draft. No randomness, no network."""
    head = "[AI-STUB DRAFT — replace ai_complete() with a real model call]"
    summary = prompt.strip().splitlines()
    excerpt = summary[0][:200] if summary else "(empty prompt)"
    return f"{head}\nContext: {excerpt}\nNote: review and confirm before filing."


def _dispatch(backend: str, prompt: str, *, system: str, max_tokens: int) -> str:
    """Hook for a real provider. Left unimplemented on purpose.

    Implement here (or import a thin client) when promoting from stub to a
    live model. Must remain optional so the default install stays std-lib
    only and offline. PHI-safe inputs only.
    """
    raise NotImplementedError(
        f"OMII_QE_AI_BACKEND='{backend}' has no implementation. "
        "Wire a provider in ai/qe/_common.py:_dispatch (see README), "
        "or unset the var to use the offline 'stub' backend."
    )


# --------------------------------------------------------------------------
# Small CLI/printing helpers
# --------------------------------------------------------------------------
def eprint(*args: object) -> None:
    print(*args, file=sys.stderr)


def md_table(headers: Iterable[str], rows: Iterable[Iterable[object]]) -> str:
    headers = list(headers)
    out = ["| " + " | ".join(headers) + " |",
           "| " + " | ".join("---" for _ in headers) + " |"]
    for row in rows:
        out.append("| " + " | ".join(str(c) for c in row) + " |")
    return "\n".join(out)
