import { Alcohol, BodyType, Diet, Gender, RelationshipType, Smoking } from '@/types/enums';

export interface Option<T extends string = string> {
  value: T;
  label: string;
}

function toOptions<T extends string>(labels: Record<T, string>): ReadonlyArray<Option<T>> {
  return (Object.keys(labels) as T[]).map((value) => ({ value, label: labels[value] }));
}

// Record<Enum, string> makes the compiler fail if the backend adds a value and the label is missing here.
export const GENDER_OPTIONS = toOptions<Gender>({ MALE: 'Man', FEMALE: 'Woman', OTHER: 'Other' });

export const BODY_TYPE_OPTIONS = toOptions<BodyType>({
  SLIM: 'Slim',
  AVERAGE: 'Average',
  ATHLETIC: 'Athletic',
  HEAVY: 'Larger build',
  NO_PREFERENCE: 'No preference',
});

export const DIET_OPTIONS = toOptions<Diet>({
  OMNIVORE: 'Omnivore',
  VEGETARIAN: 'Vegetarian',
  VEGAN: 'Vegan',
  PESCATARIAN: 'Pescatarian',
  FLEXITARIAN: 'Flexitarian',
  OTHER: 'Other',
  NO_PREFERENCE: 'No preference',
});

export const RELATIONSHIP_OPTIONS = toOptions<RelationshipType>({
  SERIOUS_RELATIONSHIP: 'A serious relationship',
  CASUAL_DATING: 'Casual dating',
  FRIENDSHIP: 'Friendship',
  NO_PREFERENCE: 'No preference',
});

export const SMOKING_OPTIONS = toOptions<Smoking>({
  NON_SMOKER: 'Non-smoker',
  OCCASIONAL: 'Occasionally',
  REGULAR: 'Regularly',
  TRYING_TO_QUIT: 'Trying to quit',
  NO_PREFERENCE: 'No preference',
});

export const ALCOHOL_OPTIONS = toOptions<Alcohol>({
  NEVER: 'Never',
  SOMETIMES: 'Sometimes',
  REGULARLY: 'Regularly',
  NO_PREFERENCE: 'No preference',
});

export const ROLE_LABEL = { USER: 'member', MODERATOR: 'moderator', MANAGER: 'manager' } as const;

export function optionLabel<T extends string>(options: ReadonlyArray<Option<T>>, value: T | null | undefined): string | null {
  if (!value) return null;
  return options.find((option) => option.value === value)?.label ?? null;
}
