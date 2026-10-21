import { createContext } from 'react';
import type { AuthResult, Session } from './types';

export type AuthState =
  | { status: 'loading' }
  | { status: 'authenticated'; session: Session }
  | { status: 'anonymous'; expired: boolean }
  /** A stored session exists but could not be checked (network, server down). Not the same as logged out. */
  | { status: 'error' };

export interface AuthContextValue {
  state: AuthState;
  signIn: (result: AuthResult) => void;
  signOut: () => Promise<void>;
  /** Retry restoring the session after status "error". */
  retry: () => void;
}

export const AuthContext = createContext<AuthContextValue | null>(null);
