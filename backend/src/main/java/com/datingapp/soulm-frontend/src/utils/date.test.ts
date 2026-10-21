import { describe, expect, it } from 'vitest';
import { calculateAge, latestAdultBirthDate, parseIsoDate, toIsoDate } from './date';

describe('parseIsoDate', () => {
  it('accepts real dates and rejects impossible or malformed ones', () => {
    expect(parseIsoDate('2000-02-29')).toEqual({ year: 2000, month: 2, day: 29 });
    expect(parseIsoDate('1999-02-29')).toBeNull();
    expect(parseIsoDate('2020-13-01')).toBeNull();
    expect(parseIsoDate('20-01-01')).toBeNull();
    expect(parseIsoDate('')).toBeNull();
  });
});

describe('calculateAge', () => {
  const birth = { year: 2000, month: 6, day: 15 };
  it('counts completed years only', () => {
    expect(calculateAge(birth, new Date(2018, 5, 14))).toBe(17); // the day before the 18th birthday
    expect(calculateAge(birth, new Date(2018, 5, 15))).toBe(18); // the birthday itself
    expect(calculateAge(birth, new Date(2018, 11, 31))).toBe(18);
  });
  it('is negative for the future', () => {
    expect(calculateAge({ year: 2030, month: 1, day: 1 }, new Date(2026, 0, 1))).toBeLessThan(0);
  });
});

describe('latestAdultBirthDate', () => {
  it('is exactly 18 years before the given day', () => {
    expect(latestAdultBirthDate(new Date(2026, 9, 18))).toBe('2008-10-18');
    expect(toIsoDate(new Date(2008, 0, 5))).toBe('2008-01-05');
  });
});
