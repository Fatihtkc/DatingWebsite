/** Enum values copied from the backend source (com.datingapp.backend.enums). */

/** Role (authority is "ROLE_" + name). */
export const Role = { USER: 'USER', MODERATOR: 'MODERATOR', MANAGER: 'MANAGER' } as const;
export type Role = (typeof Role)[keyof typeof Role];

export const NotificationType = { NEW_MATCH: 'NEW_MATCH', NEW_MESSAGE: 'NEW_MESSAGE', NEW_LIKE: 'NEW_LIKE' } as const;
export type NotificationType = (typeof NotificationType)[keyof typeof NotificationType];

export const ModerationStatus = { PENDING: 'PENDING', APPROVED: 'APPROVED', REJECTED: 'REJECTED' } as const;
export type ModerationStatus = (typeof ModerationStatus)[keyof typeof ModerationStatus];

/** Token.TokenType. Only PASSWORD_RESET is referenced by the supplied code. */
export const TokenType = { PASSWORD_RESET: 'PASSWORD_RESET' } as const;
export type TokenType = (typeof TokenType)[keyof typeof TokenType];

export const Gender = { MALE: 'MALE', FEMALE: 'FEMALE', OTHER: 'OTHER' } as const;
export type Gender = (typeof Gender)[keyof typeof Gender];

export const BodyType = {
  SLIM: 'SLIM',
  AVERAGE: 'AVERAGE',
  ATHLETIC: 'ATHLETIC',
  HEAVY: 'HEAVY',
  NO_PREFERENCE: 'NO_PREFERENCE',
} as const;
export type BodyType = (typeof BodyType)[keyof typeof BodyType];

export const Diet = {
  OMNIVORE: 'OMNIVORE',
  VEGETARIAN: 'VEGETARIAN',
  VEGAN: 'VEGAN',
  PESCATARIAN: 'PESCATARIAN',
  FLEXITARIAN: 'FLEXITARIAN',
  OTHER: 'OTHER',
  NO_PREFERENCE: 'NO_PREFERENCE',
} as const;
export type Diet = (typeof Diet)[keyof typeof Diet];

export const RelationshipType = {
  SERIOUS_RELATIONSHIP: 'SERIOUS_RELATIONSHIP',
  CASUAL_DATING: 'CASUAL_DATING',
  FRIENDSHIP: 'FRIENDSHIP',
  NO_PREFERENCE: 'NO_PREFERENCE',
} as const;
export type RelationshipType = (typeof RelationshipType)[keyof typeof RelationshipType];

export const Alcohol = { NEVER: 'NEVER', SOMETIMES: 'SOMETIMES', REGULARLY: 'REGULARLY', NO_PREFERENCE: 'NO_PREFERENCE' } as const;
export type Alcohol = (typeof Alcohol)[keyof typeof Alcohol];

/** Java enum is called Smoking; the DTO property that carries it is named `smoke`. */
export const Smoking = {
  NON_SMOKER: 'NON_SMOKER',
  OCCASIONAL: 'OCCASIONAL',
  REGULAR: 'REGULAR',
  TRYING_TO_QUIT: 'TRYING_TO_QUIT',
  NO_PREFERENCE: 'NO_PREFERENCE',
} as const;
export type Smoking = (typeof Smoking)[keyof typeof Smoking];
