# Frontend — Healthcare Web Application (SUT)

> **Status:** Delivered (initial vertical) — **Milestone 4**.
> React + TypeScript + Vite single-page app that is the primary **System Under
> Test** for the automation platform. Authentication, role-based navigation,
> patient management, and appointment booking are wired to the backend APIs.

## Purpose

A production-quality healthcare web application: the SUT the Milestone 5
automation framework drives without modification. Built for accessibility,
responsiveness, i18n, theming, and testability (stable `data-testid` selectors).

## Stack

React 18 · TypeScript · Vite · Material UI v6 · React Router · TanStack Query ·
Axios · React Hook Form · i18next · Emotion. See
[docs/PROJECT_METADATA.md](../../docs/PROJECT_METADATA.md) §3.

## Structure

```
src/
  api/         Axios client (token + refresh interceptors) and typed services
  auth/        AuthContext (login/logout, current user, permissions)
  components/  AppLayout, ErrorBoundary (reusable shell)
  i18n/        i18next setup + locale resources (no hardcoded strings)
  pages/       Login, Dashboard, Patients, Appointments, NotFound, Unauthorized
  routes/      AppRouter + ProtectedRoute (auth + permission guards)
  theme/       MUI theme + light/dark ColorMode context (persisted)
  types/       Shared API contract types
```

## Run

```bash
npm install
npm run dev        # http://localhost:5173 (proxies /api -> backend :8080)
npm run build      # tsc --noEmit && vite build
npm run lint       # eslint (flat config)
```

Sign in with the synthetic demo account: `demo.admin` / `Admin@12345` (requires
the backend running on the `dev` profile).

## Testability

Every interactive element carries a stable `data-testid`, navigation is
permission-aware, and the DOM structure is consistent — so the future automation
framework (Milestone 5) interacts with the UI without changes.

## Boundaries

- No test tooling lives here (Playwright/Selenium are Milestone 5).
- PWA/offline, additional portals (Doctor, Lab, Radiology, Pharmacy, Billing,
  Insurance, Admin), and more components are on the roadmap; this milestone
  delivers the authenticated shell plus the Patient and Appointment slices.

## References

- [ARCHITECTURE.md](../../ARCHITECTURE.md) §5 · [docs/UI_UX_SPECIFICATION.md](../../docs/UI_UX_SPECIFICATION.md)
- [docs/ACCESSIBILITY_GUIDE.md](../../docs/ACCESSIBILITY_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Frontend Engineer | Initial SUT vertical (Milestone 4) |
