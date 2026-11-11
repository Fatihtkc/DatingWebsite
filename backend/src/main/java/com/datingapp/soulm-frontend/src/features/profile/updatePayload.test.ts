import { describe, expect, it } from 'vitest';
import type { UserProfile } from '@/types/user';
import { buildUpdateRequest, toEditDefaults } from './updatePayload';

const profile: UserProfile = {
  id: 7, username: 'ada', firstName: 'Ada', lastName: 'Lovelace', birthDate: '1990-05-17', gender: 'FEMALE', height: 172.5,
  bodyType: 'ATHLETIC', location: 'Istanbul', relationshipType: 'FRIENDSHIP', alcohol: 'SOMETIMES', smoke: 'NON_SMOKER', diet: null,
  shorterBio: 'Hi', hobbies: null, favoriteMusic: null, weekendPlans: null, images: [],
};
const coords = { latitude: 41.015, longitude: 28.979 };

describe('toEditDefaults', () => {
  it('turns nulls into empty form values and leaves the two unreadable preferences empty', () => {
    const defaults = toEditDefaults(profile);
    expect(defaults).toMatchObject({ firstName: 'Ada', gender: 'FEMALE', height: '172.5', bodyType: 'ATHLETIC', diet: '', hobbies: '', agePreference: '', distancePreference: '' });
  });
});

describe('buildUpdateRequest', () => {
  it('sends the date of birth back unchanged, because the server overwrites every field', () => {
    const request = buildUpdateRequest(profile, toEditDefaults(profile), coords);
    expect(request.birthDate).toBe('1990-05-17');
    expect(request.latitude).toBe(41.015);
    expect(request.longitude).toBe(28.979);
  });

  it('leaves cleared optional fields out and trims text', () => {
    const values = { ...toEditDefaults(profile), shorterBio: '   ', bodyType: '', height: '', hobbies: '  chess ', firstName: ' Ada ' };
    const json = JSON.parse(JSON.stringify(buildUpdateRequest(profile, values, coords))) as Record<string, unknown>;
    expect(json).not.toHaveProperty('shorterBio');
    expect(json).not.toHaveProperty('bodyType');
    expect(json).not.toHaveProperty('height');
    expect(json.hobbies).toBe('chess');
    expect(json.firstName).toBe('Ada');
  });

  it('never sends username, which the service ignores', () => {
    expect(buildUpdateRequest(profile, toEditDefaults(profile), coords)).not.toHaveProperty('username');
  });

  it('refuses to build without a gender', () => {
    expect(() => buildUpdateRequest(profile, { ...toEditDefaults(profile), gender: '' }, coords)).toThrow();
  });
});
