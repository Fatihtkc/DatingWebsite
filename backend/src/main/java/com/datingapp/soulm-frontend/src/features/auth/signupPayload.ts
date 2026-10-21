import { Alcohol, BodyType, Diet, Gender, RelationshipType, Smoking } from '@/types/enums';
import type { UserCreateRequest } from '@/types/user';
import type { Coordinates } from '@/hooks/useGeolocation';
import type { AccountValues, ProfileValues } from './schemas';

function toEnum<T extends string>(values: readonly T[], value: string): T | undefined {
  return values.find((candidate) => candidate === value);
}

function textOrUndefined(value: string): string | undefined {
  const trimmed = value.trim();
  return trimmed === '' ? undefined : trimmed;
}

export function parseHeight(raw: string): number | undefined {
  const trimmed = raw.trim().replace(',', '.');
  if (trimmed === '') return undefined;
  const value = Number(trimmed);
  return Number.isFinite(value) ? value : undefined;
}

/** Turns validated form values into the exact UserCreateDTO body. Empty optional fields are left out. */
export function buildSignupRequest(account: AccountValues, profile: ProfileValues, coords: Coordinates): UserCreateRequest {
  const gender = toEnum(Object.values(Gender), profile.gender);
  if (!gender) throw new Error('Gender is required.');

  return {
    username: account.username,
    firstName: account.firstName,
    lastName: account.lastName,
    email: account.email,
    password: account.password,
    birthDate: account.birthDate,
    gender,
    height: parseHeight(profile.height),
    location: textOrUndefined(profile.location),
    shorterBio: textOrUndefined(profile.shorterBio),
    hobbies: textOrUndefined(profile.hobbies),
    favoriteMusic: textOrUndefined(profile.favoriteMusic),
    weekendPlans: textOrUndefined(profile.weekendPlans),
    bodyType: toEnum(Object.values(BodyType), profile.bodyType),
    relationshipType: toEnum(Object.values(RelationshipType), profile.relationshipType),
    agePreference: textOrUndefined(profile.agePreference),
    distancePreference: textOrUndefined(profile.distancePreference),
    smoke: toEnum(Object.values(Smoking), profile.smoke),
    alcohol: toEnum(Object.values(Alcohol), profile.alcohol),
    diet: toEnum(Object.values(Diet), profile.diet),
    latitude: coords.latitude,
    longitude: coords.longitude,
  };
}
