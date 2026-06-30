import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ColorModeProvider } from '@/theme/ColorModeContext';
import { AuthProvider } from '@/auth/AuthContext';
import { ErrorBoundary } from '@/components/ErrorBoundary';
import { AppRouter } from '@/routes/AppRouter';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: 1, refetchOnWindowFocus: false },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ColorModeProvider>
        <AuthProvider>
          <ErrorBoundary>
            <BrowserRouter>
              <AppRouter />
            </BrowserRouter>
          </ErrorBoundary>
        </AuthProvider>
      </ColorModeProvider>
    </QueryClientProvider>
  );
}
