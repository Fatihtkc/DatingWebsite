import { describe, expect, it } from 'vitest';
import { Role } from '@/types/enums';
import { pathBelongsToRole, resolvePostLoginTarget, roleHome } from './routing';

describe('roleHome', () => {
  it('maps each backend role to its area', () => {
    expect(roleHome(Role.USER)).toBe('/app');
    expect(roleHome(Role.MODERATOR)).toBe('/moderator');
    expect(roleHome(Role.MANAGER)).toBe('/manager');
  });
});

describe('pathBelongsToRole', () => {
  it('matches the area root and its children only', () => {
    expect(pathBelongsToRole('/app', Role.USER)).toBe(true);
    expect(pathBelongsToRole('/app/discover', Role.USER)).toBe(true);
    expect(pathBelongsToRole('/application', Role.USER)).toBe(false);
    expect(pathBelongsToRole('/manager', Role.USER)).toBe(false);
  });
});

describe('resolvePostLoginTarget', () => {
  const from = (pathname: string, search?: string) => ({ from: { pathname, search } });

  it('returns the page the person was bounced from when it is inside their area', () => {
    expect(resolvePostLoginTarget(Role.USER, from('/app/matches', '?page=2'))).toBe('/app/matches?page=2');
  });

  it('falls back to the role home without usable state', () => {
    expect(resolvePostLoginTarget(Role.MODERATOR, undefined)).toBe('/moderator');
    expect(resolvePostLoginTarget(Role.MODERATOR, null)).toBe('/moderator');
    expect(resolvePostLoginTarget(Role.MODERATOR, {})).toBe('/moderator');
  });

  it('never sends someone into another role area', () => {
    expect(resolvePostLoginTarget(Role.USER, from('/manager/moderators'))).toBe('/app');
  });

  it('refuses open-redirect style targets', () => {
    expect(resolvePostLoginTarget(Role.USER, from('//evil.example/app'))).toBe('/app');
    expect(resolvePostLoginTarget(Role.USER, from('https://evil.example/app'))).toBe('/app');
    expect(resolvePostLoginTarget(Role.USER, from('/app\\..\\evil'))).toBe('/app');
  });
});
