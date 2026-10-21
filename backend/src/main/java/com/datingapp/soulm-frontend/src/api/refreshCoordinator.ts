/**
 * Refresh tokens rotate and the backend treats re-use of a rotated token as theft:
 * RefreshTokenServiceImpl#refresh revokes the whole token family on re-use.
 * Two concurrent refreshes with the same token would therefore log the user out everywhere.
 *
 *  - Within a tab, all callers share one in-flight promise.
 *  - Across tabs, the Web Locks API serialises refreshes. The callback must read the refresh
 *    token from storage *inside* the lock, so a waiting tab picks up the token another tab
 *    just rotated instead of re-using the old one.
 */
const LOCK_NAME = 'soulm:token-refresh';

let inFlight: Promise<string | null> | null = null;

function withCrossTabLock<T>(task: () => Promise<T>): Promise<T> {
  const locks = typeof navigator !== 'undefined' ? navigator.locks : undefined;
  if (!locks) return task();
  return locks.request(LOCK_NAME, task) as Promise<T>;
}

/**
 * @param performRefresh resolves with the new access token, or null when the session
 *   cannot be renewed. Rejecting means "could not find out" (e.g. network down).
 */
export function refreshAccessToken(performRefresh: () => Promise<string | null>): Promise<string | null> {
  if (inFlight) return inFlight;
  const run = withCrossTabLock(performRefresh).finally(() => {
    inFlight = null;
  });
  inFlight = run;
  return run;
}
