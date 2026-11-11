import { Alcohol, BodyType, Diet, Gender, RelationshipType, Smoking } from '@/types/enums';
import type { UserProfile, UserUpdateRequest } from '@/types/user';
import type { Coordinates } from '@/hooks/useGeolocation';
import { parseHeight } from '@/features/auth/signupPayload';
import type { EditProfileValues } from './editSchema';

const str = (value: string | null | undefined) => value ?? '';

function toEnum<T extends string>(values: readonly T[], value: string): T | undefined {
  return values.find((candidate) => candidate === value);
}

function textOrUndefined(value: string): string | undefined {
  const trimmed = value.trim();
  return trimmed === '' ? undefined : trimmed;
}

/** Form values for the edit page, from the profile the server returned. agePreference and distancePreference are never returned, so they start empty. */
export function toEditDefaults(profile: UserProfile): EditProfileValues {
  return {
    firstName: str(profile.firstName),
    lastName: str(profile.lastName),
    gender: profile.gender ?? '',
    height: profile.height === null ? '' : String(profile.height),
    bodyType: profile.bodyType ?? '',
    diet: profile.diet ?? '',
    smoke: profile.smoke ?? '',
    alcohol: profile.alcohol ?? '',
    relationshipType: profile.relationshipType ?? '',
    location: str(profile.location),
    shorterBio: str(profile.shorterBio),
    hobbies: str(profile.hobbies),
    favoriteMusic: str(profile.favoriteMusic),
    weekendPlans: str(profile.weekendPlans),
    agePreference: '',
    distancePreference: '',
  };
}

/**
 * The update endpoint replaces everything with what is sent, so the date of birth (which the form does not
 * offer for editing) is sent back unchanged; omitting it would erase it. Empty optional fields are left out,
 * which the server stores as null.
 */
export function buildUpdateRequest(original: UserProfile, values: EditProfileValues, coords: Coordinates): UserUpdateRequest {
  const gender = toEnum(Object.values(Gender), values.gender);
  if (!gender) throw new Error('Gender is required.');

  return {
    firstName: values.firstName.trim(),
    lastName: values.lastName.trim(),
    birthDate: original.birthDate ?? undefined,
    gender,
    height: parseHeight(values.height),
    bodyType: toEnum(Object.values(BodyType), values.bodyType),
    diet: toEnum(Object.values(Diet), values.diet),
    smoke: toEnum(Object.values(Smoking), values.smoke),
    alcohol: toEnum(Object.values(Alcohol), values.alcohol),
    relationshipType: toEnum(Object.values(RelationshipType), values.relationshipType),
    location: textOrUndefined(values.location),
    shorterBio: textOrUndefined(values.shorterBio),
    hobbies: textOrUndefined(values.hobbies),
    favoriteMusic: textOrUndefined(values.favoriteMusic),
    weekendPlans: textOrUndefined(values.weekendPlans),
    agePreference: textOrUndefined(values.agePreference),
    distancePreference: textOrUndefined(values.distancePreference),
    latitude: coords.latitude,
    longitude: coords.longitude,
  };
}
