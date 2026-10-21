/** Expiry of a JWT in epoch milliseconds, read from its payload without verifying it, or null if unreadable. */
export function readJwtExpiry(token: string): number | null {
  const payloadPart = token.split('.')[1];
  if (!payloadPart) return null;
  try {
    const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
    const payload: unknown = JSON.parse(atob(padded));
    if (typeof payload === 'object' && payload !== null && 'exp' in payload && typeof payload.exp === 'number') {
      return payload.exp * 1000;
    }
  } catch {
    // fall through: not a JWT we can read
  }
  return null;
}
