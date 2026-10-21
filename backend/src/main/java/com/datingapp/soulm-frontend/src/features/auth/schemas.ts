import { z } from 'zod';
import { Alcohol, BodyType, Diet, Gender, RelationshipType, Smoking } from '@/types/enums';
import { calculateAge, parseIsoDate } from '@/utils/date';

export const MIN_AGE = 18;
export const HEIGHT_MIN_CM = 50;
export const HEIGHT_MAX_CM = 250;

const utf8Length = (value: string) => new TextEncoder().encode(value).length;

/**
 * Mirrors UserCreateDTO (12+ characters with a lowercase letter, an uppercase letter and a digit).
 * The 72-byte cap is stricter than the DTO's 128: bcrypt ignores everything past 72 bytes, so
 * longer input is refused instead of being silently truncated.
 * PasswordResetRequest's rules were not supplied; the reset page assumes they match.
 */
export const passwordSchema = z
  .string()
  .min(12, 'Use at least 12 characters.')
  .regex(/[a-z]/, 'Add a lowercase letter.')
  .regex(/[A-Z]/, 'Add an uppercase letter.')
  .regex(/\d/, 'Add a number.')
  // refine() returns a ZodEffects, which has no .regex(), so it must come last.
  .refine((value) => utf8Length(value) <= 72, 'That password is too long. Shorten it to 72 characters or fewer.');

export const PASSWORD_HINT = 'At least 12 characters, with an uppercase letter, a lowercase letter and a number.';

const emailSchema = z
  .string()
  .trim()
  .toLowerCase()
  .min(1, 'Enter your email address.')
  .max(254, 'That email address is too long.')
  .email('Enter a valid email address.');

export const loginSchema = z.object({
  email: emailSchema,
  password: z.string().min(1, 'Enter your password.'),
});
export type LoginValues = z.infer<typeof loginSchema>;

const name = (label: string) =>
  z
    .string()
    .trim()
    .min(2, `Enter your ${label} (at least 2 characters).`)
    .max(50, 'Use at most 50 characters.');

export const accountSchema = z
  .object({
    firstName: name('first name'),
    lastName: name('last name'),
    username: z
      .string()
      .trim()
      .min(3, 'Choose a username with at least 3 characters.')
      .max(30, 'Use at most 30 characters.'),
    email: emailSchema,
    confirmEmail: z.string().trim().toLowerCase().min(1, 'Re-enter your email address.'),
    // The backend only requires a past date. 18+ is enforced here and must also be enforced server-side.
    birthDate: z
      .string()
      .min(1, 'Enter your date of birth.')
      .superRefine((value, ctx) => {
        const date = parseIsoDate(value);
        if (!date || date.year < 1900) {
          ctx.addIssue({ code: z.ZodIssueCode.custom, message: 'Enter a valid date.' });
          return;
        }
        const age = calculateAge(date);
        if (age < 0) ctx.addIssue({ code: z.ZodIssueCode.custom, message: 'Your date of birth cannot be in the future.' });
        else if (age < MIN_AGE) ctx.addIssue({ code: z.ZodIssueCode.custom, message: `You must be at least ${MIN_AGE} to join SoulM.` });
        else if (age > 120) ctx.addIssue({ code: z.ZodIssueCode.custom, message: 'Enter a valid date.' });
      }),
    password: passwordSchema,
    confirmPassword: z.string().min(1, 'Re-enter your password.'),
  })
  .superRefine((values, ctx) => {
    if (values.email !== values.confirmEmail) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ['confirmEmail'], message: 'The email addresses do not match.' });
    }
    if (values.password !== values.confirmPassword) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ['confirmPassword'], message: 'The passwords do not match.' });
    }
  });
export type AccountValues = z.infer<typeof accountSchema>;

export const codeSchema = z.object({
  code: z.string().trim().regex(/^\d{6}$/, 'Enter the 6-digit code from your email.'),
});

const oneOf = (values: readonly string[]) => (value: string) => values.includes(value);
const optionalChoice = (values: readonly string[]) =>
  z.string().refine((value) => value === '' || oneOf(values)(value), 'Choose one of the options.');
const optionalText = (max: number) => z.string().trim().max(max, `Use at most ${max} characters.`);

function isValidHeight(raw: string): boolean {
  const trimmed = raw.trim().replace(',', '.');
  if (trimmed === '') return true;
  const value = Number(trimmed);
  return Number.isFinite(value) && value >= HEIGHT_MIN_CM && value <= HEIGHT_MAX_CM;
}

export const profileSchema = z.object({
  gender: z.string().refine(oneOf(Object.values(Gender)), 'Choose an option.'),
  height: z.string().refine(isValidHeight, `Enter a height between ${HEIGHT_MIN_CM} and ${HEIGHT_MAX_CM} cm.`),
  bodyType: optionalChoice(Object.values(BodyType)),
  diet: optionalChoice(Object.values(Diet)),
  smoke: optionalChoice(Object.values(Smoking)),
  alcohol: optionalChoice(Object.values(Alcohol)),
  relationshipType: optionalChoice(Object.values(RelationshipType)),
  location: optionalText(500),
  shorterBio: optionalText(500),
  hobbies: optionalText(1000),
  favoriteMusic: optionalText(200),
  weekendPlans: optionalText(500),
  agePreference: optionalText(500),
  distancePreference: optionalText(500),
});
export type ProfileValues = z.infer<typeof profileSchema>;

export const EMPTY_PROFILE: ProfileValues = {
  gender: '',
  height: '',
  bodyType: '',
  diet: '',
  smoke: '',
  alcohol: '',
  relationshipType: '',
  location: '',
  shorterBio: '',
  hobbies: '',
  favoriteMusic: '',
  weekendPlans: '',
  agePreference: '',
  distancePreference: '',
};
