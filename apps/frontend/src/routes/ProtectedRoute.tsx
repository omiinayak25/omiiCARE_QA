import { Navigate, Outlet } from 'react-router-dom';
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import { useAuth } from '@/auth/AuthContext';

interface Props {
  requiredAuthority?: string;
}

// Guards routes: redirects unauthenticated users to /login and authenticated
// users lacking the required permission to /unauthorized.
export function ProtectedRoute({ requiredAuthority }: Props) {
  const { isAuthenticated, initializing, hasAuthority } = useAuth();

  if (initializing) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress aria-label="loading" />
      </Box>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requiredAuthority && !hasAuthority(requiredAuthority)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
}
