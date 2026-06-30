# AI Development Rules

> **Purpose.** The binding rulebook every AI agent reads at the start of every
> session before acting in omiiCARE_QA. These rules are not advisory: an agent
> that cannot satisfy a rule stops and escalates to a human. They make
> [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md) #10 ("AI assists, humans decide")
> operational and enforceable.

## Scope

The non-negotiable rules governing AI behavior across all milestones — what to
read, what never to do, how to respect milestone fences, how to work
autonomously yet safely, and how to keep cost and quality in line. The *workflow*
these rules run inside is [AI_DEVELOPMENT_WORKFLOW.md](AI_DEVELOPMENT_WORKFLOW.md).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| AI agent | Obey every rule; stop and escalate when a rule cannot be met |
| Human reviewer | Verify rule compliance before merge |
| Maintainer | Amend rules only via ADR; own enforcement |

---

## 1. Session-Start Reading (Rules 1–5)

1. **Read the source of truth first.** At session start, read
   [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) before
   any action.
2. **Read the business rules.** Read [docs/BUSINESS_RULES.md](BUSINESS_RULES.md)
   for domain constraints before touching domain-related work.
3. **Read the canonical facts.** Read [PROJECT_METADATA.md](PROJECT_METADATA.md);
   never invent or override versions, names, or environments.
4. **Read this rulebook.** Read this `AI_DEVELOPMENT_RULES.md` in full each
   session.
5. **Read the governing docs.** Read the relevant documents under `docs/` (and
   [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md), [ARCHITECTURE.md](../ARCHITECTURE.md),
   [ROADMAP.md](../ROADMAP.md)) for the area being worked on.

## 2. Authority of Documentation (Rules 6–8)

6. **Never contradict the docs.** Documentation is the source of truth; if code
   and docs disagree, the code is the bug. Do not "fix" docs to match wrong code
   without human direction.
7. **Reconcile, don't diverge.** On any conflict between canonical documents,
   stop and escalate with the conflict and options; do not pick a side silently.
8. **Documentation-first.** Update docs/ADRs *before* implementing, every time
   ([DEVELOPMENT_WORKFLOW.md](DEVELOPMENT_WORKFLOW.md) §1).

## 3. Milestone Discipline (Rules 9–11)

9. **Respect milestone fences.** Work only within the active milestone's scope;
   never build anything the [ROADMAP.md](../ROADMAP.md) fences out (e.g. no
   application/API/automation code in Milestone 1).
10. **One milestone at a time.** Never generate the whole project at once;
    advance milestone by milestone.
11. **Fence changes need an ADR.** A fence is non-negotiable without a
    roadmap-amending ADR approved by a human.

## 4. Autonomy & Permission (Rules 12–13)

12. **Ask once, then proceed.** Request elevated permission a single time; once
    granted, continue autonomously to milestone completion without repeatedly
    asking "continue?".
13. **Stop at the boundary.** Autonomy ends at the milestone fence and at any
    guardrail in §6 — both force a stop and a hand-back to a human.

## 5. Quality & Output (Rules 14–18)

14. **No placeholders.** Never emit Lorem Ipsum, TODO sections, stubs presented
    as complete, or fabricated content.
15. **Every commit is clean.** Every commit compiles, passes tests (from M2
    onward), and is exactly **one logical change** with a Conventional Commit
    message.
16. **Meet the Definition of Done.** Satisfy the applicable
    [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) checklist before declaring work
    done.
17. **Follow the standards.** Obey [CODING_STANDARDS.md](CODING_STANDARDS.md),
    [REPOSITORY_STANDARDS.md](REPOSITORY_STANDARDS.md), and apply patterns from
    [DESIGN_PATTERNS.md](DESIGN_PATTERNS.md) only where they reduce complexity.
18. **Be transparent.** Label AI-assisted work, disclose what was AI-produced,
    and log the prompt and model version
    ([AI_DEVELOPMENT_WORKFLOW.md](AI_DEVELOPMENT_WORKFLOW.md) §4, §6).

## 6. Security Guardrails (Rules 19–23)

19. **No secrets.** Never read, write, generate, embed, or log secrets,
    credentials, tokens, or keys. Use configuration injection (Principle #3).
20. **No real PHI.** Use only synthetic, PHI-safe data; never introduce real
    patient data.
21. **No unreviewed merges.** Never merge, push to a protected branch, or
    self-approve. Humans decide (Principle #10).
22. **No fabricated compliance.** Never claim HIPAA certification or FHIR/HL7
    conformance beyond what the docs explicitly state; the project makes **no
    formal certification claims** ([MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §9).
23. **Security by default.** Authorization on every endpoint, least privilege,
    secure defaults; flag any change that weakens these.

## 7. Cost Awareness (Rules 24–26)

24. **Local-first.** Extract structure with file/search tools before invoking
    expensive reasoning; gather context once.
25. **Batch & single-pass.** Read related files together; produce output in a
    single pass rather than iterative churn.
26. **Concise artifacts.** Prefer tables and bullets; no redundant re-analysis of
    unchanged files.

## 8. Escalation (Rule 27)

27. **Escalate, don't guess.** When a rule cannot be satisfied — document
    conflict, fence crossing, secret/dependency need, or compliance claim — stop,
    state the issue and options, and let a human decide.

## Examples

- Session start: the agent reads Rules §1 in order, confirms the active milestone
  in [ROADMAP.md](../ROADMAP.md), and only then plans work.
- Asked to scaffold a controller during Milestone 1, the agent refuses (Rule 9)
  and escalates (Rule 27).
- Asked to hardcode a token to unblock a test, the agent refuses (Rule 19) and
  proposes configuration injection.

## Future Enhancements

- A machine-checkable subset of these rules enforced in CI (label present, no
  secrets committed, fence not crossed) — Milestone 8.
- A session-start checklist surfaced automatically to agents.

## Dependencies

- Operationalized by [AI_DEVELOPMENT_WORKFLOW.md](AI_DEVELOPMENT_WORKFLOW.md).
- Anchored by [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md),
  [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md) #10, and
  [PROJECT_METADATA.md](PROJECT_METADATA.md).
- Enforced via [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) and
  [REPOSITORY_STANDARDS.md](REPOSITORY_STANDARDS.md).

## References

- [SECURITY.md](../SECURITY.md), [ROADMAP.md](../ROADMAP.md),
  [CONTRIBUTING.md](../CONTRIBUTING.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial binding AI development rules (Milestone 1) |
