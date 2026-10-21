import { describe, expect, it } from 'vitest';
import type { NearbyUser } from '@/types/social';
import { buildQueue, hasActiveFilters, matchesFilters, NO_FILTERS } from './filters';

const today = new Date(2026, 5, 15);

function user(id: number, overrides: Partial<NearbyUser> = {}): NearbyUser {
  return {
    id, username: `u${id}`, firstName: `U${id}`, lastName: null, birthDate: '1996-06-15', gender: 'FEMALE', height: null, bodyType: null,
    location: null, distance: 3, relationshipType: 'FRIENDSHIP', alcohol: null, smoke: null, diet: null, shorterBio: null, hobbies: null,
    favoriteMusic: null, weekendPlans: null, images: null, ...overrides,
  };
}

describe('matchesFilters', () => {
  it('lets everyone through with no filters', () => {
    expect(matchesFilters(user(1, { birthDate: null }), NO_FILTERS, today)).toBe(true);
  });

  it('applies the age range inclusively, using completed years', () => {
    const filters = { ...NO_FILTERS, minAge: '30', maxAge: '30' };
    expect(matchesFilters(user(1, { birthDate: '1996-06-15' }), filters, today)).toBe(true); // turns 30 today
    expect(matchesFilters(user(1, { birthDate: '1996-06-16' }), filters, today)).toBe(false); // 29 until tomorrow
    expect(matchesFilters(user(1, { birthDate: '1995-06-15' }), filters, today)).toBe(false); // 31
  });

  it('hides people of unknown age while an age filter is on', () => {
    expect(matchesFilters(user(1, { birthDate: null }), { ...NO_FILTERS, minAge: '18' }, today)).toBe(false);
  });

  it('filters by gender and relationship type', () => {
    expect(matchesFilters(user(1, { gender: 'MALE' }), { ...NO_FILTERS, gender: 'FEMALE' }, today)).toBe(false);
    expect(matchesFilters(user(1), { ...NO_FILTERS, relationshipType: 'FRIENDSHIP' }, today)).toBe(true);
    expect(matchesFilters(user(1), { ...NO_FILTERS, relationshipType: 'SERIOUS_RELATIONSHIP' }, today)).toBe(false);
  });

  it('ignores blank or non-numeric ages', () => {
    expect(matchesFilters(user(1), { ...NO_FILTERS, minAge: ' ', maxAge: 'abc' }, today)).toBe(true);
  });
});

describe('hasActiveFilters', () => {
  it('is false only when every filter is blank', () => {
    expect(hasActiveFilters(NO_FILTERS)).toBe(false);
    expect(hasActiveFilters({ ...NO_FILTERS, gender: 'MALE' })).toBe(true);
  });
});

describe('buildQueue', () => {
  it('drops people already seen, duplicates and filtered-out people, keeping order', () => {
    const users = [user(1), user(2), user(2), user(3, { gender: 'MALE' }), user(4)];
    const queue = buildQueue(users, new Set([1]), { ...NO_FILTERS, gender: 'FEMALE' }, today);
    expect(queue.map((u) => u.id)).toEqual([2, 4]);
  });
});
