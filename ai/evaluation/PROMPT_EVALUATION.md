# Prompt Evaluation

> **Purpose.** Define how omiiCARE_QA evaluates AI outputs and prompt quality so
> the prompt library stays accurate, hallucination-resistant, and reviewable
> before any prompt version is pinned in production. Evaluation makes the
> "explainable and reviewable" principle measurable: a prompt earns its version
> by passing this rubric, not by assumption.

## Scope

- The dimensions every AI output is scored on.
- Hallucination and PHI/secret-leak checks.
- The reviewer rubric and pass criteria.
- Golden examples and regression of prompts across versions.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| AI QA Engineer | Maintain golden examples and the rubric; run prompt regression |
| Human reviewer | Score outputs; approve a prompt version for pinning |
| Maintainer | Gate prompt-version advances on evaluation results |

---

## 1. Evaluation Dimensions

Every AI output is scored on these dimensions (1–5 each):

| Dimension | Question | Fail trigger |
|-----------|----------|--------------|
| Accuracy | Are the facts/claims correct and grounded? | Any fabricated fact |
| Hallucination resistance | Does it avoid inventing rules/data/steps? | Invented `BR-*`, repro steps, or coverage claim |
| Completeness | Does it cover required cases (negative/boundary/RBAC/healthcare)? | Missing mandated case type |
| Traceability | Is each item traced to a `BR-*`/AC/RTM? | Untraced safety-critical item |
| Format compliance | Does it match the prompt's OUTPUT contract? | Unparseable / contract break |
| Safety | Synthetic-only, no PHI/secrets, unsafe code warned? | Any PHI/secret leak (auto-fail) |
| Confidence calibration | Is the stated confidence justified by evidence? | Overconfident on thin evidence |
| Usefulness | Would a reviewer accept it as a strong draft to edit? | Net rework exceeds value |

---

## 2. Hallucination Checks

- **Grounding check:** every factual claim must trace to provided input, a
  documented `BR-*`, or canonical facts. Ungrounded claims are flagged.
- **Invention check:** scenarios/repro-steps/coverage percentages presented as fact
  (not estimate/assumption) fail.
- **Rule-fidelity check:** any cited `BR-*` is verified against
  `docs/BUSINESS_RULES.md`; a non-existent rule fails.
- **FHIR-fidelity check:** code-system URIs and required fields verified against
  FHIR R4; wrong URI fails.
- **Assumption surfacing:** the output must label assumptions and open questions
  rather than hiding them.

---

## 3. PHI / Secret-Leak Check (auto-fail)

- Scan inputs and outputs for PHI patterns (names, MRNs, DOB, contact, ids) and
  secret patterns (keys, tokens, passwords).
- Any real-looking PHI or any secret in input or output is an **automatic fail**
  and a security event (`AI_SECURITY_GUARDRAILS.md`).
- Verify redaction markers (`[REDACTED-PHI]`/`[REDACTED-SECRET]`) are present where
  expected.

---

## 4. Reviewer Rubric

A reviewer scores each output and records:

```
PROMPT: <id> v<version>   PROVIDER/MODEL: <...>
SCORES (1-5): accuracy, hallucination, completeness, traceability,
              format, safety, confidence-calibration, usefulness
AUTO-FAILS: <PHI/secret leak? fabricated fact? contract break?>
VERDICT: PASS | PASS-WITH-EDITS | FAIL
NOTES: <what to fix in the prompt or the output>
REVIEWER: <human>   DATE: <date>
```

**Pass criteria to pin a prompt version:**

- No auto-fails on any golden example.
- Mean score ≥ 4 on accuracy, hallucination resistance, traceability, and safety.
- Format compliance = 5 (the engine parses it) on every golden example.

---

## 5. Golden Examples

- Each prompt has a **golden set**: representative inputs paired with an
  expected-quality reference output (or acceptance criteria for the output).
- Golden inputs are **synthetic** and cover: a happy case, a negative/edge case, a
  healthcare-specific case (audit/consent/tenancy/FHIR), and a "missing-input" case
  (to confirm the prompt flags rather than invents).
- Golden examples live alongside this doc in `ai/evaluation/` and are versioned with
  the prompt.

---

## 6. Prompt Regression

- **When:** before advancing any `omii.ai.prompt.<capability>.version`, and on a
  scheduled cadence.
- **How:** re-run the golden set through the candidate prompt version and score
  against the rubric.
- **Cross-provider:** run the golden set on each supported provider (Claude/OpenAI/
  local) the capability may use, to confirm provider-neutrality.
- **Gate:** a version advances only if it meets pass criteria and does not regress
  any dimension versus the current pinned version. Regressions block the bump.
- **Record:** results are stored with prompt ID/version, provider/model, scores, and
  reviewer — feeding the audit trail and the knowledge base.

---

## 7. Continuous Signal

- Sample real (reviewed) invocations periodically and score them with the same
  rubric to catch drift.
- Feed recurring weaknesses back into the prompt (a reviewed prompt change),
  re-evaluate, and re-pin.

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
