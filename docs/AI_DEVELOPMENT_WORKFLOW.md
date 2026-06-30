# AI Development Workflow

> **Purpose.** Describe how AI agents contribute to omiiCARE_QA: when AI is used,
> the cycle it follows (identical to the human one), the mandatory human review of
> every AI output, the transparency required of AI-assisted artifacts, the
> guardrails it operates under, and how it escalates to humans. AI assists;
> **humans decide** ([PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md) #10).

## Scope

The process for AI-assisted contribution across all milestones. The *binding
rules* an agent must obey are enumerated in
[AI_DEVELOPMENT_RULES.md](AI_DEVELOPMENT_RULES.md); this document describes the
*workflow* those rules run inside. It complements, never overrides, the human
[DEVELOPMENT_WORKFLOW.md](DEVELOPMENT_WORKFLOW.md).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| AI agent | Follow the cycle and rules; produce transparent, reviewable output |
| Human reviewer | Review every AI artifact before merge; own the decision |
| Maintainer | Approve AI-assisted milestone transitions; own guardrail policy |
| QA Architect | Keep AI workflow aligned with the human workflow and DoD |

---

## 1. When AI Is Used, Per Milestone

AI is **optional** and assistive. Typical uses by milestone:

| Milestone | Representative AI assistance |
|-----------|------------------------------|
| M1 | Drafting governance docs, consistency checks, cross-link auditing |
| M2 | Drafting config/compose scaffolding for human review |
| M3 | Boilerplate (DTOs, mappers, tests) within the documented design |
| M4 | Component scaffolds, a11y checks, copy/i18n drafts |
| M5–M7 | Test-case drafts, adapter stubs, failure-analysis suggestions |
| M8 | Workflow/pipeline drafts for review |
| M9 | Provider-abstracted AI assistants themselves (transparent, reviewable) |
| M10 | Audit assistance, documentation reconciliation |

AI is never used to make an unreviewed change, and never to manufacture a
compliance or certification claim.

## 2. The Same Cycle

AI follows the identical seven-step cycle as humans
([DEVELOPMENT_WORKFLOW.md](DEVELOPMENT_WORKFLOW.md) §1):

```
Design ─▶ Document ─▶ Review ─▶ Implement ─▶ Test ─▶ Refactor ─▶ Document
```

Differences for AI: the **Review** step is a *mandatory human gate* (not optional
peer review), and the agent must surface its assumptions and rationale at each
step so a human can verify them.

## 3. Mandatory Human Review

- **Every** AI-produced artifact — doc, code, test, config — is reviewed by a
  human before merge. No AI output self-merges (M9 fence;
  [ROADMAP.md](../ROADMAP.md)).
- The reviewer applies the same adversarial standard as for human changes and
  verifies the [Definition of Done](DEFINITION_OF_DONE.md).
- The human reviewer is accountable for the merged result; the AI is a tool, not
  an approver.

## 4. Transparency & Labeling

- AI-assisted pull requests carry the `status:ai-assisted` label
  ([REPOSITORY_STANDARDS.md](REPOSITORY_STANDARDS.md) §9).
- The PR description states what the AI produced and what the human changed.
- AI-generated artifacts are explainable: the agent records why it chose an
  approach so a reviewer can audit the reasoning.
- No artifact is presented as human-authored when it is AI-assisted.

## 5. Guardrails

| Guardrail | Rule |
|-----------|------|
| Secrets | Never read, write, generate, or log secrets/credentials/PHI |
| Merges | Never merge, push to protected branches, or self-approve |
| Compliance | Never fabricate HIPAA/FHIR certification or conformance claims |
| Scope | Never cross the active milestone fence |
| Docs authority | Never contradict the canonical docs; reconcile or escalate |
| Determinism | Every produced change must compile/pass and be one logical change |
| Data | Use only synthetic, PHI-safe data |

## 6. Prompt & Version Logging

- The originating prompt/intent and the agent + model version are recorded with
  the change (PR description or commit trailer) for auditability.
- This log supports reproducibility and the Milestone 10 documentation audit.
- Logs never contain secrets or PHI.

## 7. Autonomy Boundary

Per [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §4, an
agent asks for elevated permission **once**; after it is granted, it proceeds
autonomously to **milestone completion** without repeatedly asking "continue?".
Autonomy ends at the milestone fence and at any guardrail in §5 — both force a
stop and a hand-back to a human.

## 8. Escalation to Humans

The agent stops and escalates when it encounters:

- a conflict or ambiguity between canonical documents,
- a request that crosses a milestone fence,
- a need to introduce a dependency, secret, or external integration,
- a compliance/certification claim, or
- any guardrail in §5.

Escalation states the conflict and proposed options; a human decides.

## Examples

- During Milestone 1 an agent drafts this document, labels the PR
  `status:ai-assisted`, logs the prompt and model version, and a human reviews
  and merges it (§3–§6).
- Asked to "add a login API" in Milestone 1, the agent declines and escalates:
  the request crosses the M1 fence (§7–§8).
- Asked to embed an API key to make a test pass, the agent refuses on the secrets
  guardrail and proposes configuration-based injection (§5).

## Future Enhancements

- A PR template section auto-populated with the AI prompt/model log.
- CI validation that AI-assisted PRs carry the label and the disclosure section
  (Milestone 8).
- The Milestone 9 AI platform exposing its own transparency/coverage reports.

## Dependencies

- Bound by [AI_DEVELOPMENT_RULES.md](AI_DEVELOPMENT_RULES.md) (the rulebook).
- Mirrors [DEVELOPMENT_WORKFLOW.md](DEVELOPMENT_WORKFLOW.md); enforces
  [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md).
- Governed by [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md) #10 and
  [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md).

## References

- [SECURITY.md](../SECURITY.md), [ROADMAP.md](../ROADMAP.md) §M9.
- [PROJECT_METADATA.md](PROJECT_METADATA.md) §3 (AI Platform).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial AI development workflow (Milestone 1) |
