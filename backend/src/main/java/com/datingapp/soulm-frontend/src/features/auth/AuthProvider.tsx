import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { registerAuthHandlers } from '@/api/axiosInstance';
import { REFRESH_TOKEN_KEY, tokenStore } from '@/api/tokenStore';
import * as authApi from './authApi';
import { AuthContext, type AuthContextValue, type AuthState } from './authContext';
import { applyAuthResult, clearSession, getCurrentSession, renewSession } from './sessionManager';
import type { AuthResult } from './types';

export function AuthProvider({ children }: { children: ReactNode }) {
  const queryClient = useQueryClient();
  const [state, setState] = useState<AuthState>({ status: 'loading' });
  const [attempt, setAttempt] = useState(0);

  // Give the Axios layer its refresh and "session is over" behaviour.
  useEffect(() => {
    registerAuthHandlers({
      refreshSession: renewSession,
      onSessionExpired: () => {
        clearSession();
        queryClient.removeQueries(); // never keep one person's data around for the next
        setState({ status: 'anonymous', expired: true });
      },
    });
  }, [queryClient]);

  // Restore the session on page load: the access token lives in memory only, so trade the stored refresh token for one.
  useEffect(() => {
    let cancelled = false;

    async function restore() {
      if (!tokenStore.getRefreshToken()) {
        setState({ status: 'anonymous', expired: false });
        return;
      }
      setState({ status: 'loading' });
      try {
        const token = await renewSession();
        if (cancelled) return;
        const session = getCurrentSession();
        if (token && session) {
          setState({ status: 'authenticated', session });
        } else {
          clearSession();
          setState({ status: 'anonymous', expired: false });
        }
      } catch {
        if (!cancelled) setState({ status: 'error' });
      }
    }

    void restore();
    return () => {
      cancelled = true;
    };
  }, [attempt]);

  // Logging out in one tab logs out the others.
  useEffect(() => {
    function onStorage(event: StorageEvent) {
      if (event.key === REFRESH_TOKEN_KEY && event.newValue === null) {
        clearSession();
        queryClient.removeQueries();
        setState({ status: 'anonymous', expired: false });
      }
    }
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, [queryClient]);

  const signIn = useCallback(
    (result: AuthResult) => {
      queryClient.removeQueries();
      applyAuthResult(result);
      setState({ status: 'authenticated', session: result.session });
    },
    [queryClient],
  );

  const signOut = useCallback(async () => {
    const refreshToken = tokenStore.getRefreshToken();
    clearSession();
    queryClient.removeQueries();
    setState({ status: 'anonymous', expired: false });
    if (refreshToken) {
      try {
        await authApi.logout(refreshToken);
      } catch {
        // Best effort: the device is already signed out; the server-side token simply expires on its own.
      }
    }
  }, [queryClient]);

  const retry = useCallback(() => setAttempt((value) => value + 1), []);

  const value = useMemo<AuthContextValue>(() => ({ state, signIn, signOut, retry }), [state, signIn, signOut, retry]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
