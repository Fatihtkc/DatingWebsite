import type { NearbyUser } from '@/types/social';
import { calculateAge, parseIsoDate } from '@/utils/date';

/**
 * The nearby endpoint filters by distance only, so these are applied in the browser to the people
 * already loaded. Selects hold '' for "any".
 */
export interface DiscoverFilters {
  minAge: string;
  maxAge: string;
  gender: string;
  relationshipType: string;
}

export const NO_FILTERS: DiscoverFilters = { minAge: '', maxAge: '', gender: '', relationshipType: '' };

export function hasActiveFilters(filters: DiscoverFilters): boolean {
  return Object.values(filters).some((value) => value.trim() !== '');
}

function toAge(raw: string): number | null {
  const trimmed = raw.trim();
  if (trimmed === '') return null;
  const value = Number(trimmed);
  return Number.isInteger(value) && value >= 0 ? value : null;
}

export function matchesFilters(
  user: Pick<NearbyUser, 'birthDate' | 'gender' | 'relationshipType'>,
  filters: DiscoverFilters,
  today: Date = new Date(),
): boolean {
  const min = toAge(filters.minAge);
  const max = toAge(filters.maxAge);
  if (min !== null || max !== null) {
    const birth = user.birthDate ? parseIsoDate(user.birthDate) : null;
    if (!birth) return false; // an age filter is active and this person's age is unknown
    const age = calculateAge(birth, today);
    if (min !== null && age < min) return false;
    if (max !== null && age > max) return false;
  }
  if (filters.gender !== '' && user.gender !== filters.gender) return false;
  if (filters.relationshipType !== '' && user.relationshipType !== filters.relationshipType) return false;
  return true;
}

/** People still to show: not already liked or passed this session, matching the filters, no duplicates. */
export function buildQueue(
  users: readonly NearbyUser[],
  seen: ReadonlySet<number>,
  filters: DiscoverFilters,
  today: Date = new Date(),
): NearbyUser[] {
  const ids = new Set<number>();
  const queue: NearbyUser[] = [];
  for (const user of users) {
    if (seen.has(user.id) || ids.has(user.id) || !matchesFilters(user, filters, today)) continue;
    ids.add(user.id);
    queue.push(user);
  }
  return queue;
}
