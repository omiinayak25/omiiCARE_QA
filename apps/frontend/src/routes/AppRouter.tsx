import { Navigate, Route, Routes } from 'react-router-dom';
import { ProtectedRoute } from './ProtectedRoute';
import { AppLayout } from '@/components/AppLayout';
import { LoginPage } from '@/pages/LoginPage';
import { DashboardPage } from '@/pages/DashboardPage';
import { PatientsPage } from '@/pages/PatientsPage';
import { AppointmentsPage } from '@/pages/AppointmentsPage';
import { NotFoundPage } from '@/pages/NotFoundPage';
import { UnauthorizedPage } from '@/pages/UnauthorizedPage';

// Central route table. Authenticated routes are nested under the app layout and
// gated by ProtectedRoute; permission-specific routes pass requiredAuthority.
export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/" element={<DashboardPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute requiredAuthority="patient:read" />}>
        <Route element={<AppLayout />}>
          <Route path="/patients" element={<PatientsPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute requiredAuthority="appointment:read" />}>
        <Route element={<AppLayout />}>
          <Route path="/appointments" element={<AppointmentsPage />} />
        </Route>
      </Route>

      <Route path="/404" element={<NotFoundPage />} />
      <Route path="*" element={<Navigate to="/404" replace />} />
    </Routes>
  );
}
