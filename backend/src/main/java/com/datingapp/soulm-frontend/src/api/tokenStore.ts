/**
 * Token storage under the backend's actual contract.
 *
 * The API issues a bearer JWT (15 min) plus a rotating refresh token (30 days) in the
 * response body, and CORS is configured with allowCredentials=false, so HttpOnly cookies
 * are not available without a backend change.
 *
 *  - Access token: module memory only. Lost on reload, recovered through the refresh token.
 *  - Refresh token: localStorage. This survives reloads but is readable by any script on
 *    the page, so an XSS bug would expose it. Keep dependencies lean, avoid
 *    dangerouslySetInnerHTML and ship a strict Content-Security-Policy.
 *
 * Passwords, OTPs and reset tokens are never stored.
 */
export const REFRESH_TOKEN_KEY = 'soulm.refreshToken';

let accessToken: string | null = null;

function readStorage(): string | null {
  try {
    return window.localStorage.getItem(REFRESH_TOKEN_KEY);
  } catch {
    return null; // storage disabled (private mode, blocked cookies)
  }
}

function writeStorage(value: string | null): void {
  try {
    if (value === null) window.localStorage.removeItem(REFRESH_TOKEN_KEY);
    else window.localStorage.setItem(REFRESH_TOKEN_KEY, value);
  } catch {
    // Nothing to do: the session simply won't survive a reload.
  }
}

export const tokenStore = {
  getAccessToken: (): string | null => accessToken,
  setAccessToken(token: string | null): void {
    accessToken = token;
  },
  getRefreshToken: (): string | null => readStorage(),
  setRefreshToken(token: string | null): void {
    writeStorage(token);
  },
  clear(): void {
    accessToken = null;
    writeStorage(null);
  },
};
