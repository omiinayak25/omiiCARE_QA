import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { fetchCurrentUser, login as loginRequest } from '@/api/services';
import { tokenStore } from '@/api/client';
import type { CurrentUser } from '@/types';

interface AuthContextValue {
  user: CurrentUser | null;
  initializing: boolean;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  hasAuthority: (authority: string) => boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [initializing, setInitializing] = useState(true);

  useEffect(() => {
    let active = true;
    async function bootstrap() {
      if (tokenStore.access) {
        try {
          const current = await fetchCurrentUser();
          if (active) {
            setUser(current);
          }
        } catch {
          tokenStore.clear();
        }
      }
      if (active) {
        setInitializing(false);
      }
    }
    void bootstrap();
    return () => {
      active = false;
    };
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    const tokens = await loginRequest(username, password);
    tokenStore.set(tokens.accessToken, tokens.refreshToken);
    setUser(await fetchCurrentUser());
  }, []);

  const logout = useCallback(() => {
    tokenStore.clear();
    setUser(null);
  }, []);

  const hasAuthority = useCallback(
    (authority: string) => Boolean(user?.authorities.includes(authority)),
    [user],
  );

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      initializing,
      isAuthenticated: Boolean(user),
      login,
      logout,
      hasAuthority,
    }),
    [user, initializing, login, logout, hasAuthority],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
