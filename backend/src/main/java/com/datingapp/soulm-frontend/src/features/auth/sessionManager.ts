import { normalizeApiError } from '@/api/errors';
import { refreshAccessToken } from '@/api/refreshCoordinator';
import { tokenStore } from '@/api/tokenStore';
import { readJwtExpiry } from '@/utils/jwt';
import * as authApi from './authApi';
import type { AuthResult, Session } from './types';

let currentSession: Session | null = null;

export function getCurrentSession(): Session | null {
  return currentSession;
}

export function applyAuthResult(result: AuthResult): void {
  tokenStore.setAccessToken(result.accessToken);
  tokenStore.setRefreshToken(result.refreshToken);
  currentSession = result.session;
}

export function clearSession(): void {
  tokenStore.clear();
  currentSession = null;
}

/**
 * Resolves with a new access token, or null when the server rejected the refresh token (or none
 * is stored). Throws when the outcome is unknown (network failure, 5xx, 429), so the session is
 * kept. The refresh token is read here, inside the cross-tab lock, on purpose.
 */
async function renew(): Promise<string | null> {
  const stored = tokenStore.getRefreshToken();
  if (!stored) return null;

  try {
    const result = await authApi.refresh(stored);
    applyAuthResult(result);
    return result.accessToken;
  } catch (error) {
    const apiError = normalizeApiError(error);
    const status = apiError.status ?? 0;
    const rejected = apiError.kind === 'http' && status >= 400 && status < 500 && status !== 429;
    if (rejected) return null;
    throw apiError;
  }
}

/** Single-flight, cross-tab-safe session renewal. Used by the Axios 401 handler and by app start-up. */
export function renewSession(): Promise<string | null> {
  return refreshAccessToken(renew);
}

const EXPIRY_MARGIN_MS = 30_000;

/**
 * An access token that is good for at least another 30 seconds, renewing it first if needed.
 * Used before opening the WebSocket, which authenticates only once, at CONNECT.
 * Resolves null when the session is gone; throws when renewal could not be attempted (network).
 */
export async function getValidAccessToken(): Promise<string | null> {
  const token = tokenStore.getAccessToken();
  if (token) {
    const expiresAt = readJwtExpiry(token);
    if (expiresAt !== null && expiresAt - Date.now() > EXPIRY_MARGIN_MS) return token;
  }
  return renewSession();
}
