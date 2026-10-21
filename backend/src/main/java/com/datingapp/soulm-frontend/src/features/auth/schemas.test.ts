import { describe, expect, it } from 'vitest';
import { latestAdultBirthDate } from '@/utils/date';
import { accountSchema, passwordSchema, profileSchema, EMPTY_PROFILE } from './schemas';

const validAccount = {
  firstName: 'Ada',
  lastName: 'Lovelace',
  username: 'ada',
  email: 'ada@example.com',
  confirmEmail: 'ada@example.com',
  birthDate: '1990-05-17',
  password: 'Correct-Horse-9',
  confirmPassword: 'Correct-Horse-9',
};

function firstMessage(result: { success: boolean; error?: { issues: Array<{ message: string }> } }): string | undefined {
  return result.success ? undefined : result.error?.issues[0]?.message;
}

describe('passwordSchema (mirrors UserCreateDTO)', () => {
  it('accepts 12+ characters with upper, lower and a digit', () => {
    expect(passwordSchema.safeParse('Abcdefghij1k').success).toBe(true);
  });

  it.each([
    ['Abcdefgh1jk', 'Use at least 12 characters.'], // 11 characters
    ['abcdefghijk1l', 'Add an uppercase letter.'],
    ['ABCDEFGHIJK1L', 'Add a lowercase letter.'],
    ['Abcdefghijklm', 'Add a number.'],
  ])('rejects %s', (value, message) => {
    expect(firstMessage(passwordSchema.safeParse(value))).toBe(message);
  });

  it('refuses more than 72 bytes instead of letting bcrypt truncate', () => {
    expect(passwordSchema.safeParse(`Aa1${'x'.repeat(70)}`).success).toBe(false);
  });
});

describe('accountSchema', () => {
  it('accepts a valid account and normalises the email', () => {
    const parsed = accountSchema.safeParse({ ...validAccount, email: '  Ada@Example.com ', confirmEmail: 'ada@example.com' });
    expect(parsed.success).toBe(true);
    if (parsed.success) expect(parsed.data.email).toBe('ada@example.com');
  });

  it('flags mismatched email and password confirmations on the confirmation field', () => {
    const parsed = accountSchema.safeParse({ ...validAccount, confirmEmail: 'other@example.com', confirmPassword: 'nope' });
    expect(parsed.success).toBe(false);
    if (!parsed.success) {
      const paths = parsed.error.issues.map((issue) => issue.path.join('.'));
      expect(paths).toContain('confirmEmail');
      expect(paths).toContain('confirmPassword');
    }
  });

  it('rejects under-18s, future dates and impossible dates', () => {
    expect(accountSchema.safeParse({ ...validAccount, birthDate: '2999-01-01' }).success).toBe(false);
    expect(accountSchema.safeParse({ ...validAccount, birthDate: '1990-02-31' }).success).toBe(false);
    const tooYoung = new Date();
    tooYoung.setFullYear(tooYoung.getFullYear() - 17);
    const iso = `${tooYoung.getFullYear()}-${String(tooYoung.getMonth() + 1).padStart(2, '0')}-${String(tooYoung.getDate()).padStart(2, '0')}`;
    expect(firstMessage(accountSchema.safeParse({ ...validAccount, birthDate: iso }))).toBe('You must be at least 18 to join SoulM.');
  });

  it('accepts someone who turns 18 today', () => {
    expect(accountSchema.safeParse({ ...validAccount, birthDate: latestAdultBirthDate() }).success).toBe(true);
  });

  it('enforces the DTO length limits', () => {
    expect(accountSchema.safeParse({ ...validAccount, username: 'ab' }).success).toBe(false);
    expect(accountSchema.safeParse({ ...validAccount, username: 'x'.repeat(31) }).success).toBe(false);
    expect(accountSchema.safeParse({ ...validAccount, firstName: 'A' }).success).toBe(false);
  });
});

describe('profileSchema', () => {
  it('requires only gender', () => {
    expect(profileSchema.safeParse(EMPTY_PROFILE).success).toBe(false);
    expect(profileSchema.safeParse({ ...EMPTY_PROFILE, gender: 'FEMALE' }).success).toBe(true);
  });

  it('rejects values that are not backend enum members', () => {
    expect(profileSchema.safeParse({ ...EMPTY_PROFILE, gender: 'FEMALE', diet: 'CARNIVORE' }).success).toBe(false);
    expect(profileSchema.safeParse({ ...EMPTY_PROFILE, gender: 'FEMALE', smoke: 'REGULAR' }).success).toBe(true);
  });

  it('keeps height within 50-250 and accepts a decimal comma', () => {
    const base = { ...EMPTY_PROFILE, gender: 'MALE' };
    expect(profileSchema.safeParse({ ...base, height: '49' }).success).toBe(false);
    expect(profileSchema.safeParse({ ...base, height: '251' }).success).toBe(false);
    expect(profileSchema.safeParse({ ...base, height: '172,5' }).success).toBe(true);
  });

  it('enforces text length limits', () => {
    const base = { ...EMPTY_PROFILE, gender: 'MALE' };
    expect(profileSchema.safeParse({ ...base, shorterBio: 'x'.repeat(501) }).success).toBe(false);
    expect(profileSchema.safeParse({ ...base, favoriteMusic: 'x'.repeat(201) }).success).toBe(false);
  });
});
