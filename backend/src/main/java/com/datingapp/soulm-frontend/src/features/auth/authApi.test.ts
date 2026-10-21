import { describe, expect, it } from 'vitest';
import { ApiError } from '@/api/errors';
import { parseAuthResponse } from './authApi';

describe('parseAuthResponse (JwtResponseDTO)', () => {
  it('maps the backend field names, including `token` for the access token', () => {
    expect(parseAuthResponse({ id: 7, username: 'ada', token: 'a.b.c', refreshToken: 'r', role: 'USER' })).toEqual({
      session: { userId: 7, role: 'USER', accountName: 'ada' },
      accessToken: 'a.b.c',
      refreshToken: 'r',
    });
  });

  it('rejects unknown roles and missing tokens instead of guessing', () => {
    expect(() => parseAuthResponse({ id: 1, username: 'x', token: 't', refreshToken: 'r', role: 'ADMIN' })).toThrow(ApiError);
    expect(() => parseAuthResponse({ id: 1, username: 'x', accessToken: 't', refreshToken: 'r', role: 'USER' })).toThrow(ApiError);
    expect(() => parseAuthResponse(null)).toThrow(ApiError);
  });
});
