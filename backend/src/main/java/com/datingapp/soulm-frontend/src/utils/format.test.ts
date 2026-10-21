import { describe, expect, it } from 'vitest';
import { ageFromBirthDate, displayName, formatDistance, nextPageParam, truncate } from './format';

describe('formatDistance', () => {
  it('rounds sensibly and handles missing values', () => {
    expect(formatDistance(0.4)).toBe('Less than 1 km away');
    expect(formatDistance(3.14159)).toBe('3.1 km away');
    expect(formatDistance(42.6)).toBe('43 km away');
    expect(formatDistance(null)).toBeNull();
    expect(formatDistance(Number.NaN)).toBeNull();
  });
});

describe('ageFromBirthDate', () => {
  it('returns whole years, or null for missing or invalid dates', () => {
    expect(ageFromBirthDate('2000-06-15', new Date(2026, 5, 15))).toBe(26);
    expect(ageFromBirthDate('2000-06-16', new Date(2026, 5, 15))).toBe(25);
    expect(ageFromBirthDate(null)).toBeNull();
    expect(ageFromBirthDate('nonsense')).toBeNull();
    expect(ageFromBirthDate('2999-01-01')).toBeNull();
  });
});

describe('displayName', () => {
  it('prefers the first name and never shows the last name', () => {
    expect(displayName({ firstName: ' Ada ', username: 'ada99' })).toBe('Ada');
    expect(displayName({ firstName: null, username: 'ada99' })).toBe('ada99');
    expect(displayName({ firstName: '  ', username: 'ada99' })).toBe('ada99');
  });
});

describe('truncate / nextPageParam', () => {
  it('truncates with an ellipsis only when needed', () => {
    expect(truncate('short', 10)).toBe('short');
    expect(truncate('a long sentence here', 10)).toBe('a long se\u2026');
  });
  it('advances until the last page', () => {
    expect(nextPageParam({ last: false, number: 2 })).toBe(3);
    expect(nextPageParam({ last: true, number: 2 })).toBeUndefined();
  });
});
