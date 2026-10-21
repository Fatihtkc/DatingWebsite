import { MAX_DISTANCE_KM, MIN_DISTANCE_KM } from './discoverApi';

const SEEN_KEY = 'soulm.discover.seen';
const DISTANCE_KEY = 'soulm.discover.distanceKm';
const MAX_SEEN = 1000;
const DEFAULT_DISTANCE_KM = 50; // the endpoint's own default

/*
 * Passing on someone has no backend endpoint. Who was liked or passed is remembered for this
 * browser session only (sessionStorage), so passed people may come back in a later session.
 * Neither value is sensitive.
 */

export function loadSeen(): Set<number> {
  try {
    const parsed: unknown = JSON.parse(window.sessionStorage.getItem(SEEN_KEY) ?? '[]');
    if (Array.isArray(parsed)) return new Set(parsed.filter((value): value is number => typeof value === 'number'));
  } catch {
    // storage unavailable or corrupt: start fresh
  }
  return new Set();
}

export function saveSeen(seen: ReadonlySet<number>): void {
  try {
    window.sessionStorage.setItem(SEEN_KEY, JSON.stringify([...seen].slice(-MAX_SEEN)));
  } catch {
    // not persisted; still works for this page view
  }
}

export function loadDistance(): number {
  try {
    const value = Number(window.localStorage.getItem(DISTANCE_KEY));
    if (Number.isFinite(value) && value >= MIN_DISTANCE_KM && value <= MAX_DISTANCE_KM) return Math.round(value);
  } catch {
    // ignore
  }
  return DEFAULT_DISTANCE_KM;
}

export function saveDistance(distanceKm: number): void {
  try {
    window.localStorage.setItem(DISTANCE_KEY, String(distanceKm));
  } catch {
    // ignore
  }
}
