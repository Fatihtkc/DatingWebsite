import axios, { type AxiosInstance } from 'axios';
import { env } from '@/config/env';
import { normalizeApiError } from './errors';
import { refreshAccessToken } from './refreshCoordinator';
import { tokenStore } from './tokenStore';

const REQUEST_TIMEOUT_MS = 20_000;

const baseConfig = {
  baseURL: env.apiBaseUrl,
  timeout: REQUEST_TIMEOUT_MS,
  headers: { Accept: 'application/json' },
} as const;

/** Wired up by the auth feature, so this module needs no knowledge of the login/refresh DTOs. */
export interface AuthHandlers {
  /**
   * Exchange the stored refresh token for a new session and store it.
   * Resolve with the new access token, or null when the server rejected the refresh token.
   * Reject for transient failures (network, 5xx) so the session is kept.
   * Must use `publicHttp`, never `http`.
   */
  refreshSession: () => Promise<string | null>;
  /** Called once per failed refresh. Clear session state and send the person to the login page. */
  onSessionExpired: () => void;
}

let authHandlers: AuthHandlers | null = null;

export function registerAuthHandlers(handlers: AuthHandlers): void {
  authHandlers = handlers;
}

function createNormalizingClient(): AxiosInstance {
  const client = axios.create(baseConfig);
  client.interceptors.response.use(undefined, (error: unknown) => Promise.reject(normalizeApiError(error)));
  return client;
}

/**
 * For endpoints that must not carry a bearer token or trigger a refresh:
 * login, signup, refresh, logout, OTP, password recovery.
 */
export const publicHttp = createNormalizingClient();

/** Replays a request once after a refresh. Has no 401 handling, so it cannot loop. */
const replayHttp = createNormalizingClient();

/** For everything that needs a signed-in user. Rejects only with ApiError. */
export const http = axios.create(baseConfig);

http.interceptors.request.use((config) => {
  const token = tokenStore.getAccessToken();
  if (token) config.headers.set('Authorization', `Bearer ${token}`);
  return config;
});

http.interceptors.response.use(undefined, async (error: unknown) => {
  const apiError = normalizeApiError(error);
  const handlers = authHandlers;

  if (apiError.status !== 401 || !handlers || !axios.isAxiosError(error) || !error.config) {
    throw apiError;
  }

  // A 401 means the request was rejected before reaching any controller, so replaying it
  // after a refresh cannot duplicate a side effect, even for POST/PUT/DELETE.
  const config = error.config;
  const sentAuthorization = config.headers.get('Authorization');
  const currentToken = tokenStore.getAccessToken();

  let token: string | null;
  if (currentToken && sentAuthorization !== `Bearer ${currentToken}`) {
    token = currentToken; // another request already refreshed while this one was in flight
  } else {
    try {
      token = await refreshAccessToken(async () => {
        const renewed = await handlers.refreshSession();
        if (!renewed) handlers.onSessionExpired();
        return renewed;
      });
    } catch (refreshError) {
      throw normalizeApiError(refreshError);
    }
  }

  if (!token) throw apiError;

  config.headers.set('Authorization', `Bearer ${token}`);
  return replayHttp.request(config);
});
