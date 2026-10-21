import { describe, expect, it } from 'vitest';
import { EMPTY_PROFILE } from './schemas';
import { buildSignupRequest, parseHeight } from './signupPayload';

const account = {
  firstName: 'Ada',
  lastName: 'Lovelace',
  username: 'ada',
  email: 'ada@example.com',
  confirmEmail: 'ada@example.com',
  birthDate: '1990-05-17',
  password: 'Correct-Horse-9',
  confirmPassword: 'Correct-Horse-9',
};
const coords = { latitude: 41.015, longitude: 28.979 };

describe('buildSignupRequest', () => {
  it('sends the required fields and leaves empty optionals out of the JSON', () => {
    const request = buildSignupRequest(account, { ...EMPTY_PROFILE, gender: 'FEMALE' }, coords);
    const json = JSON.parse(JSON.stringify(request)) as Record<string, unknown>;

    expect(json).toEqual({
      username: 'ada',
      firstName: 'Ada',
      lastName: 'Lovelace',
      email: 'ada@example.com',
      password: 'Correct-Horse-9',
      birthDate: '1990-05-17',
      gender: 'FEMALE',
      latitude: 41.015,
      longitude: 28.979,
    });
    expect(json).not.toHaveProperty('confirmPassword');
    expect(json).not.toHaveProperty('confirmEmail');
  });

  it('maps filled optionals to typed enum values and numbers, using the DTO property names', () => {
    const request = buildSignupRequest(
      account,
      { ...EMPTY_PROFILE, gender: 'OTHER', height: '172,5', smoke: 'TRYING_TO_QUIT', relationshipType: 'FRIENDSHIP', shorterBio: '  Hi  ' },
      coords,
    );
    expect(request.height).toBe(172.5);
    expect(request.smoke).toBe('TRYING_TO_QUIT');
    expect(request.relationshipType).toBe('FRIENDSHIP');
    expect(request.shorterBio).toBe('Hi');
  });

  it('refuses to build a request without a valid gender', () => {
    expect(() => buildSignupRequest(account, EMPTY_PROFILE, coords)).toThrow();
  });
});

describe('parseHeight', () => {
  it('handles empty, decimal comma and junk', () => {
    expect(parseHeight('')).toBeUndefined();
    expect(parseHeight('180')).toBe(180);
    expect(parseHeight('180,5')).toBe(180.5);
    expect(parseHeight('abc')).toBeUndefined();
  });
});
