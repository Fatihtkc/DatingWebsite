import { Role } from '@/types/enums';

export const ROLE_HOME: Record<Role, string> = {
  [Role.USER]: '/app',
  [Role.MODERATOR]: '/moderator',
  [Role.MANAGER]: '/manager',
};

export function roleHome(role: Role): string {
  return ROLE_HOME[role];
}

function isSafeInternalPath(path: string): boolean {
  return path.startsWith('/') && !path.startsWith('//') && !path.includes('\\');
}

/** True when `path` lies inside the area that `role` is allowed to use. */
export function pathBelongsToRole(path: string, role: Role): boolean {
  const base = ROLE_HOME[role];
  return path === base || path.startsWith(`${base}/`);
}

/**
 * Where to send someone who has just authenticated. Honours the page they were bounced from
 * (the `from` location RequireAuth stores in router state) only if it is a same-site path inside
 * their own role's area; everything else falls back to the role's home.
 */
export function resolvePostLoginTarget(role: Role, routerState: unknown): string {
  const home = ROLE_HOME[role];
  if (typeof routerState !== 'object' || routerState === null) return home;

  const from = (routerState as { from?: { pathname?: unknown; search?: unknown } }).from;
  if (!from || typeof from.pathname !== 'string') return home;
  if (!isSafeInternalPath(from.pathname) || !pathBelongsToRole(from.pathname, role)) return home;

  return from.pathname + (typeof from.search === 'string' && from.search.startsWith('?') ? from.search : '');
}
