export interface CalendarDate {
  year: number;
  month: number; // 1-12
  day: number;
}

/** Parses a strict yyyy-MM-dd string (the format of <input type="date"> and of LocalDate JSON). */
export function parseIsoDate(value: string): CalendarDate | null {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value);
  if (!match) return null;
  const year = Number(match[1] ?? '');
  const month = Number(match[2] ?? '');
  const day = Number(match[3] ?? '');
  const probe = new Date(year, month - 1, day);
  if (probe.getFullYear() !== year || probe.getMonth() !== month - 1 || probe.getDate() !== day) return null;
  return { year, month, day };
}

/** Whole years completed on `today`. Negative when the date is in the future. */
export function calculateAge(birth: CalendarDate, today: Date = new Date()): number {
  const month = today.getMonth() + 1;
  const hadBirthdayThisYear = month > birth.month || (month === birth.month && today.getDate() >= birth.day);
  return today.getFullYear() - birth.year - (hadBirthdayThisYear ? 0 : 1);
}

export function toIsoDate(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

/** Latest date of birth that is still 18 or older today; used as the `max` of the date input. */
export function latestAdultBirthDate(today: Date = new Date()): string {
  return toIsoDate(new Date(today.getFullYear() - 18, today.getMonth(), today.getDate()));
}
