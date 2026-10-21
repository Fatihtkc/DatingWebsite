import { describe, expect, it } from 'vitest';
import { readJwtExpiry } from './jwt';

function token(payload: unknown): string {
  const encode = (value: unknown) => btoa(JSON.stringify(value)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  return `${encode({ alg: 'HS512' })}.${encode(payload)}.signature`;
}

describe('readJwtExpiry', () => {
  it('returns exp in milliseconds', () => {
    expect(readJwtExpiry(token({ sub: 'a@b.c', exp: 1_800_000_000 }))).toBe(1_800_000_000_000);
  });
  it('copes with base64url payloads that need padding', () => {
    expect(readJwtExpiry(token({ sub: 'ünïcode@example.com'.replace(/[^\x00-\x7f]/g, 'x'), exp: 5 }))).toBe(5000);
  });
  it('returns null for anything unreadable', () => {
    expect(readJwtExpiry('not-a-jwt')).toBeNull();
    expect(readJwtExpiry('a.@@@.c')).toBeNull();
    expect(readJwtExpiry(token({ sub: 'x' }))).toBeNull();
    expect(readJwtExpiry(token({ exp: 'soon' }))).toBeNull();
  });
});
