import { calculateAge, parseIsoDate } from './date';

export function ageFromBirthDate(iso: string | null | undefined, today: Date = new Date()): number | null {
  if (!iso) return null;
  const date = parseIsoDate(iso);
  if (!date) return null;
  const age = calculateAge(date, today);
  return age >= 0 && age < 130 ? age : null;
}

/** `distance` comes from the nearby endpoint in kilometres. */
export function formatDistance(km: number | null | undefined): string | null {
  if (km === null || km === undefined || !Number.isFinite(km)) return null;
  if (km < 1) return 'Less than 1 km away';
  if (km < 10) return `${km.toFixed(1)} km away`;
  return `${Math.round(km)} km away`;
}

/** First name when there is one, otherwise the username. Last names are not shown. */
export function displayName(person: { firstName: string | null; username: string }): string {
  const first = person.firstName?.trim();
  return first ? first : person.username;
}

export function truncate(text: string, max: number): string {
  return text.length <= max ? text : `${text.slice(0, max - 1).trimEnd()}\u2026`;
}

/** `sentAt` is a zone-less LocalDateTime; it is shown as written, in the viewer's locale. */
export function formatMessageTime(iso: string, now: Date = new Date()): string {
  const date = new Date(iso);
  if (Number.isNaN(date.getTime())) return '';
  const sameDay = date.toDateString() === now.toDateString();
  const options: Intl.DateTimeFormatOptions = sameDay
    ? { hour: '2-digit', minute: '2-digit' }
    : { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' };
  return new Intl.DateTimeFormat(undefined, options).format(date);
}

export function nextPageParam(page: { last: boolean; number: number }): number | undefined {
  return page.last ? undefined : page.number + 1;
}

/** Calendar date of a LocalDate or LocalDateTime string, in the viewer's locale; empty when unreadable. */
export function formatDate(iso: string | null | undefined): string {
  if (!iso) return '';
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? '' : new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(date);
}

export function fullName(person: { firstName: string | null; lastName: string | null }): string {
  return [person.firstName, person.lastName].filter((part): part is string => !!part && part.trim() !== '').join(' ');
}
